package com.playtomic.tests.wallet.application.usecase;

import com.playtomic.tests.wallet.infrastructure.mapper.WalletMapper;
import com.playtomic.tests.wallet.application.usecase.impl.GetWalletUseCaseImpl;
import com.playtomic.tests.wallet.infrastructure.exception.DocumentNotFoundException;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.infrastructure.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetWalletUseCaseImplTest {

    @InjectMocks
    private GetWalletUseCaseImpl getWalletUseCase;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @Test
    void getWallet_ok() {
        Wallet wallet = new Wallet();
        wallet.setId("id");
        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setId("id");

        when(walletRepository.findById("id")).thenReturn(Optional.of(wallet));
        when(walletMapper.toWalletResponse(wallet)).thenReturn(walletResponse);

        WalletResponse response = getWalletUseCase.getWallet("id");

        assertEquals("id", response.getId());
        verify(walletRepository).findById("id");
    }

    @Test
    void getWallet_documentNotFoundException() {
        when(walletRepository.findById("id")).thenReturn(Optional.empty());

        DocumentNotFoundException exception = assertThrows(DocumentNotFoundException.class, () -> {
            getWalletUseCase.getWallet("id");
        });

        assertEquals("The Wallet with id id does not exist", exception.getMessage());
        verify(walletRepository).findById("id");
    }

}
