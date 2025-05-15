package com.playtomic.tests.wallet.application.usecase;

import com.playtomic.tests.wallet.application.usecase.impl.TopUpWalletUseCaseImpl;
import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.event.TopUpRequestedEvent;
import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.infrastructure.exception.DocumentNotFoundException;
import com.playtomic.tests.wallet.infrastructure.mapper.WalletMapper;
import com.playtomic.tests.wallet.infrastructure.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.CreditCardNumber;
import com.playtomic.tests.wallet.presentation.dto.TopUpWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopUpWalletUseCaseImplTest {

    @InjectMocks
    private TopUpWalletUseCaseImpl topUpWalletUseCase;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private CreateTransactionUseCase createTransactionUseCase;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void topUpWallet_ok() {
        TopUpWalletRequest request = new TopUpWalletRequest();
        request.setWalletId("id");
        request.setTotal(BigDecimal.valueOf(50));
        request.setCreditCardNumber(new CreditCardNumber("1111111111111111"));

        Wallet wallet = new Wallet();
        wallet.setId("id");
        wallet.setTotalBalance(BigDecimal.ZERO);

        Transaction transaction = new Transaction();

        WalletResponse expectedResponse = new WalletResponse();
        expectedResponse.setId("id");
        expectedResponse.setTotalBalance(BigDecimal.valueOf(150));

        when(walletRepository.findById("id")).thenReturn(Optional.of(wallet));
        when(createTransactionUseCase.createTransaction(request, TransactionStatus.PENDING)).thenReturn(transaction);
        when(walletMapper.toWalletResponse(wallet)).thenReturn(expectedResponse);

        WalletResponse response = topUpWalletUseCase.topUpWallet(request);

        assertNotNull(response);
        assertEquals(expectedResponse.getTotalBalance(), response.getTotalBalance());
        verify(eventPublisher).publishEvent(any(TopUpRequestedEvent.class));
    }

    @Test
    void topUpWallet_documentNotFoundException() {
        TopUpWalletRequest request = new TopUpWalletRequest();
        request.setWalletId("-");
        request.setTotal(BigDecimal.valueOf(50));
        request.setCreditCardNumber(new CreditCardNumber("1111111111111111"));

        when(walletRepository.findById("-")).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> topUpWalletUseCase.topUpWallet(request));
        verify(walletRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

}
