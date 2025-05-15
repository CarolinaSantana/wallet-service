package com.playtomic.tests.wallet.application.usecase.impl;

import com.playtomic.tests.wallet.application.mapper.WalletMapper;
import com.playtomic.tests.wallet.application.usecase.GetWalletUseCase;
import com.playtomic.tests.wallet.domain.exception.DocumentNotFoundException;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.domain.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetWalletUseCaseImpl implements GetWalletUseCase {

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    @Autowired
    public GetWalletUseCaseImpl(
            WalletRepository walletRepository,
            WalletMapper walletMapper){
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public WalletResponse getWallet(String id) {
        Optional<Wallet> wallet = walletRepository.findById(id);
        if (wallet.isEmpty()) {
            throw new DocumentNotFoundException(String.format("The Wallet with id %s does not exist", id));
        }
        return walletMapper.toWalletResponse(wallet.get());
    }

}
