package com.playtomic.tests.wallet.application.usecase.impl;

import com.playtomic.tests.wallet.application.usecase.CreateTransactionUseCase;
import com.playtomic.tests.wallet.application.usecase.TopUpWalletUseCase;
import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.event.TopUpRequestedEvent;
import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.infrastructure.exception.DocumentNotFoundException;
import com.playtomic.tests.wallet.infrastructure.mapper.WalletMapper;
import com.playtomic.tests.wallet.infrastructure.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.TopUpWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopUpWalletUseCaseImpl implements TopUpWalletUseCase {

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    private final CreateTransactionUseCase createTransactionUseCase;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public WalletResponse topUpWallet(TopUpWalletRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new DocumentNotFoundException(
                        String.format("Wallet with id %s not found", request.getWalletId())));

        Transaction pendingTransaction = createTransactionUseCase.createTransaction(
                request, TransactionStatus.PENDING);

        String operationId = request.getOperationId();
        if (operationId == null || operationId.isBlank()) {
            operationId = UUID.randomUUID().toString();
        }
        eventPublisher.publishEvent(new TopUpRequestedEvent(this, pendingTransaction,
                wallet, request.getCreditCardNumber().value(), operationId));

        return walletMapper.toWalletResponse(wallet);
    }

}
