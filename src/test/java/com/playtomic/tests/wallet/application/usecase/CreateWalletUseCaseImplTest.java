package com.playtomic.tests.wallet.application.usecase;

import com.playtomic.tests.wallet.application.mapper.WalletMapper;
import com.playtomic.tests.wallet.application.usecase.impl.CreateWalletUseCaseImpl;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.CreateWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWalletUseCaseImplTest {

    @InjectMocks
    private CreateWalletUseCaseImpl createWalletUseCase;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMapper walletMapper;

    @Test
    void createWallet_ok() {

        CreateWalletRequest request = new CreateWalletRequest("alias", "userId");
        Wallet walletToSave = new Wallet();
        Wallet savedWallet = new Wallet();
        savedWallet.setId("id");
        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setId("id");

        when(walletMapper.toWallet(request)).thenReturn(walletToSave);
        when(walletRepository.save(walletToSave)).thenReturn(savedWallet);
        when(walletMapper.toWalletResponse(savedWallet)).thenReturn(walletResponse);

        WalletResponse response = createWalletUseCase.createWallet(request);

        assertEquals("id", response.getId());
        verify(walletRepository).save(walletToSave);
    }

}

