package com.playtomic.tests.wallet.application.usecase.impl;

import com.playtomic.tests.wallet.infrastructure.mapper.WalletMapper;
import com.playtomic.tests.wallet.application.usecase.CreateWalletUseCase;
import com.playtomic.tests.wallet.domain.enums.WalletStatus;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.infrastructure.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.CreateWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class CreateWalletUseCaseImpl implements CreateWalletUseCase {

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    @Override
    public WalletResponse createWallet(CreateWalletRequest request) {
        Wallet wallet = walletMapper.toWallet(request);
        wallet.setTotalBalance(BigDecimal.ZERO);
        wallet.setAvailableBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(new Date());
        wallet.setStatus(WalletStatus.PENDING);
        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toWalletResponse(savedWallet);
    }
}
