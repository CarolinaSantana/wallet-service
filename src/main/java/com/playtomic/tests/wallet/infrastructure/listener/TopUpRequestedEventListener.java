package com.playtomic.tests.wallet.infrastructure.listener;

import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.event.RefundRequestedEvent;
import com.playtomic.tests.wallet.domain.event.TopUpRequestedEvent;
import com.playtomic.tests.wallet.domain.exception.TransactionFailedException;
import com.playtomic.tests.wallet.domain.model.ProcessedOperation;
import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.infrastructure.client.StripeService;
import com.playtomic.tests.wallet.infrastructure.persistence.WalletPersistenceService;
import com.playtomic.tests.wallet.infrastructure.repository.ProcessedOperationRepository;
import com.playtomic.tests.wallet.infrastructure.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class TopUpRequestedEventListener {

    private final TransactionRepository transactionRepository;

    private final StripeService stripeService;

    private final WalletPersistenceService walletPersistenceService;

    private final ApplicationEventPublisher eventPublisher;

    private final ProcessedOperationRepository processedOperationRepository;

    @Autowired
    public TopUpRequestedEventListener(TransactionRepository transactionRepository,
                                       StripeService stripeService,
                                       WalletPersistenceService walletPersistenceService,
                                       ApplicationEventPublisher eventPublisher,
                                       ProcessedOperationRepository processedOperationRepository) {
        this.transactionRepository = transactionRepository;
        this.stripeService = stripeService;
        this.walletPersistenceService = walletPersistenceService;
        this.eventPublisher = eventPublisher;
        this.processedOperationRepository = processedOperationRepository;
    }

    @EventListener
    @Transactional
    public void on(TopUpRequestedEvent event) {

        Wallet wallet = event.getWallet();
        String operationId = event.getOperationId();

        try {
            processedOperationRepository.insert(new ProcessedOperation(wallet.getId(), operationId));
        } catch (DuplicateKeyException e) {
            log.warn("DuplicateKeyException. Operation {} already processed, skipping", operationId);
            return;
        }

        Transaction transaction = event.getTransaction();

        try {
            stripeService.charge(event.getCreditCardNumber(), transaction.getTotal());

            transaction.setStatus(TransactionStatus.ACCEPTED);
            transactionRepository.save(transaction);

            // An alternative is update the totalBalance once the pending transaction is created and at this moment only the availableBalance
            walletPersistenceService.updateWallet(wallet, transaction.getTotal(), transaction.getTotal());

        } catch (TransactionFailedException ex) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason(ex.getMessage());
            transactionRepository.save(transaction);

        } catch (OptimisticLockingFailureException ex) {
            log.error("OptimisticLockingFailureException. Concurrency conflict while updating wallet {} during top-up for transaction {}. Will trigger refund.",
                    wallet.getId(), transaction.getId(), ex);

            String refundRequestedEventOperationId = "refund-" + transaction.getReference();

            eventPublisher.publishEvent(new RefundRequestedEvent(
                    this,
                    transaction.getWalletId(),
                    transaction.getReference(),
                    transaction.getTotal(),
                    refundRequestedEventOperationId
            ));
        }
    }

}
