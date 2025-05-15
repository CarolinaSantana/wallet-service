package com.playtomic.tests.wallet.application.usecase;

import com.playtomic.tests.wallet.application.usecase.impl.CreateTransactionUseCaseImpl;
import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.infrastructure.mapper.TransactionMapper;
import com.playtomic.tests.wallet.infrastructure.repository.TransactionRepository;
import com.playtomic.tests.wallet.presentation.dto.CreditCardNumber;
import com.playtomic.tests.wallet.presentation.dto.TopUpWalletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTransactionUseCaseImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private CreateTransactionUseCaseImpl createTransactionUseCase;

    @Test
    void createTransaction_ok() {
        TopUpWalletRequest request = new TopUpWalletRequest();
        request.setWalletId("id");
        request.setTotal(BigDecimal.valueOf(50));
        request.setCreditCardNumber(new CreditCardNumber("1111111111111111"));

        Transaction mappedTransaction = new Transaction();
        mappedTransaction.setTotal(request.getTotal());
        mappedTransaction.setStatus(TransactionStatus.PENDING);
        mappedTransaction.setCreatedAt(null);

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId("transactionId");
        savedTransaction.setTotal(request.getTotal());
        savedTransaction.setStatus(TransactionStatus.PENDING);
        savedTransaction.setCreatedAt(new Date());

        when(transactionMapper.toTransaction(request)).thenReturn(mappedTransaction);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        Transaction result = createTransactionUseCase.createTransaction(request, TransactionStatus.PENDING);
        assertNotNull(result);
        assertEquals("transactionId", result.getId());
        assertEquals(request.getTotal(), result.getTotal());
        assertEquals(TransactionStatus.PENDING, result.getStatus());
        assertNotNull(result.getCreatedAt());
        verify(transactionMapper).toTransaction(request);
        verify(transactionRepository).save(mappedTransaction);
    }

}
