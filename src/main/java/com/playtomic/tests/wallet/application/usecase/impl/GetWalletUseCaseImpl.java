package com.playtomic.tests.wallet.application.usecase.impl;

import com.playtomic.tests.wallet.infrastructure.mapper.WalletMapper;
import com.playtomic.tests.wallet.application.usecase.GetWalletUseCase;
import com.playtomic.tests.wallet.infrastructure.exception.DocumentNotFoundException;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.infrastructure.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetWalletUseCaseImpl implements GetWalletUseCase {

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    @Override
    public WalletResponse getWallet(String id) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException(String.format(
                        "The Wallet with id %s does not exist", id)));
        return walletMapper.toWalletResponse(wallet);
    }

}
