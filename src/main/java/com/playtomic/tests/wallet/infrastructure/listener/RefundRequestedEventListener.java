package com.playtomic.tests.wallet.infrastructure.listener;

import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.enums.TransactionType;
import com.playtomic.tests.wallet.domain.event.RefundRequestedEvent;
import com.playtomic.tests.wallet.domain.model.ProcessedOperation;
import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.infrastructure.client.StripeService;
import com.playtomic.tests.wallet.infrastructure.exception.StripeServiceException;
import com.playtomic.tests.wallet.infrastructure.repository.ProcessedOperationRepository;
import com.playtomic.tests.wallet.infrastructure.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Component
public class RefundRequestedEventListener {

    private final StripeService stripeService;

    private final TransactionRepository transactionRepository;

    private final ProcessedOperationRepository processedOperationRepository;

    public RefundRequestedEventListener(StripeService stripeService,
                                        TransactionRepository transactionRepository,
                                        ProcessedOperationRepository processedOperationRepository) {
        this.stripeService = stripeService;
        this.transactionRepository = transactionRepository;
        this.processedOperationRepository = processedOperationRepository;
    }

    @EventListener
    public void on(RefundRequestedEvent event) {

        String walletId = event.getWalletId();
        String operationId = event.getOperationId();

        try {
            processedOperationRepository.insert(new ProcessedOperation(walletId, operationId));
        } catch (DuplicateKeyException e) {
            log.warn("DuplicateKeyException. Operation {} already processed, skipping", operationId);
            return;
        }

        try {
            stripeService.refund(event.getReference());

            Transaction refundTransaction = buildRefundTransaction(
                    walletId,
                    event.getTotal(),
                    event.getReference(),
                    TransactionStatus.ACCEPTED,
                    null
            );
            transactionRepository.save(refundTransaction);

        } catch (StripeServiceException e) {
            log.error("StripeServiceException. Refund failed for reference {}: {}", event.getReference(),
                    e.getMessage(), e);

            Transaction failedRefund = buildRefundTransaction(
                    walletId,
                    event.getTotal(),
                    event.getReference(),
                    TransactionStatus.FAILED,
                    e.getMessage()
            );
            transactionRepository.save(failedRefund);
        }

    }

    private Transaction buildRefundTransaction(String walletId,
                                               BigDecimal total,
                                               String reference,
                                               TransactionStatus status,
                                               String failureReason) {
        Transaction transaction = new Transaction();
        transaction.setWalletId(walletId);
        transaction.setTotal(total);
        transaction.setType(TransactionType.REFUND);
        transaction.setStatus(status);
        transaction.setReference(reference);
        transaction.setFailureReason(failureReason);
        transaction.setCreatedAt(new Date());
        return transaction;
    }

}
