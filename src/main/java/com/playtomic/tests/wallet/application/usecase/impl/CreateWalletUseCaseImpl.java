package com.playtomic.tests.wallet.application.usecase.impl;

import com.playtomic.tests.wallet.application.mapper.WalletMapper;
import com.playtomic.tests.wallet.application.usecase.CreateWalletUseCase;
import com.playtomic.tests.wallet.domain.enums.WalletStatus;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.CreateWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class CreateWalletUseCaseImpl implements CreateWalletUseCase {

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    @Autowired
    public CreateWalletUseCaseImpl(
            WalletRepository walletRepository,
            WalletMapper walletMapper){
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

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
