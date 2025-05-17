package com.playtomic.tests.wallet.infrastructure.listener;

import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.enums.TransactionType;
import com.playtomic.tests.wallet.domain.event.RefundRequestedEvent;
import com.playtomic.tests.wallet.domain.event.TopUpRequestedEvent;
import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.infrastructure.client.StripeService;
import com.playtomic.tests.wallet.infrastructure.dto.Payment;
import com.playtomic.tests.wallet.infrastructure.exception.StripeServiceException;
import com.playtomic.tests.wallet.infrastructure.persistence.WalletPersistenceService;
import com.playtomic.tests.wallet.infrastructure.repository.ProcessedOperationRepository;
import com.playtomic.tests.wallet.infrastructure.repository.TransactionRepository;
import com.playtomic.tests.wallet.infrastructure.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class WalletEventListenerIT {

    @Autowired
    private TopUpRequestedEventListener topUpRequestedEventListener;

    @Autowired
    private RefundRequestedEventListener refundRequestedEventListener;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProcessedOperationRepository processedOperationRepository;

    @Autowired
    private WalletPersistenceService walletPersistenceService;

    @MockBean
    private StripeService stripeService;

    @MockBean
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        processedOperationRepository.deleteAll();
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        when(stripeService.charge(any(), any())).thenReturn(new Payment("1"));

    }

    @Test
    void topUpRequestedEvent_isIdempotent_whenProcessedTwice() {

        Wallet wallet = new Wallet();
        wallet.setUserId("userId");
        wallet.setTotalBalance(BigDecimal.ZERO);
        wallet.setAvailableBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setReference("1");
        transaction.setTotal(new BigDecimal("100.0"));
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        TopUpRequestedEvent event = new TopUpRequestedEvent(this, transaction, wallet,
                "1111111111111111", "operationId");

        topUpRequestedEventListener.on(event);
        topUpRequestedEventListener.on(event);

        var updatedTx = transactionRepository.findById(transaction.getId()).orElseThrow();
        var updatedWallet = walletRepository.findById(wallet.getId()).orElseThrow();

        assertThat(updatedTx.getStatus()).isEqualTo(TransactionStatus.ACCEPTED);
        assertThat(updatedWallet.getTotalBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(updatedWallet.getAvailableBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(processedOperationRepository.count()).isEqualTo(1);
        verify(eventPublisher, times(0)).publishEvent(any());
        verify(stripeService, times(1)).charge(any(), any());
    }

    @Test
    void refundRequestedEvent_createsAcceptedTransaction_whenStripeRefundOk() {
        Wallet wallet = new Wallet();
        wallet.setUserId("userId");
        wallet.setTotalBalance(BigDecimal.ZERO);
        wallet.setAvailableBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);

        String operationId = "operationIdOk";
        String reference = "reference1";
        BigDecimal refundAmount = BigDecimal.valueOf(100);

        RefundRequestedEvent event = new RefundRequestedEvent(this, wallet.getId(), reference, refundAmount, operationId);

        doNothing().when(stripeService).refund(reference);

        refundRequestedEventListener.on(event);

        var txs = transactionRepository.findAll();
        assertThat(txs).hasSize(1);
        Transaction tx = txs.getFirst();
        assertThat(tx.getWalletId()).isEqualTo(wallet.getId());
        assertThat(tx.getTotal()).isEqualByComparingTo(refundAmount);
        assertThat(tx.getType()).isEqualTo(TransactionType.REFUND);
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.ACCEPTED);
        assertThat(tx.getFailureReason()).isNull();

        assertThat(processedOperationRepository.count()).isEqualTo(1);
    }

    @Test
    void refundRequestedEvent_createsFailedTransaction_whenStripeRefundException() {
        Wallet wallet = new Wallet();
        wallet.setUserId("userId");
        wallet.setTotalBalance(BigDecimal.ZERO);
        wallet.setAvailableBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);

        String operationId = "operationIdException";
        String reference = "reference2";
        BigDecimal refundAmount = BigDecimal.valueOf(100);

        RefundRequestedEvent event = new RefundRequestedEvent(this, wallet.getId(), reference, refundAmount, operationId);

        doThrow(new StripeServiceException()).when(stripeService).refund(reference);

        refundRequestedEventListener.on(event);


        var txs = transactionRepository.findAll();
        assertThat(txs).hasSize(1);
        Transaction tx = txs.getFirst();
        assertThat(tx.getWalletId()).isEqualTo(wallet.getId());
        assertThat(tx.getTotal()).isEqualByComparingTo(refundAmount);
        assertThat(tx.getType()).isEqualTo(TransactionType.REFUND);
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.FAILED);

        assertThat(processedOperationRepository.count()).isEqualTo(1);
    }

}
