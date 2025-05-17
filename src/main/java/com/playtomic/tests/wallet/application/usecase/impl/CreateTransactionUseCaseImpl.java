package com.playtomic.tests.wallet.application.usecase.impl;

import com.playtomic.tests.wallet.infrastructure.mapper.TransactionMapper;
import com.playtomic.tests.wallet.application.usecase.CreateTransactionUseCase;
import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.infrastructure.repository.TransactionRepository;
import com.playtomic.tests.wallet.presentation.dto.TopUpWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.TransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class CreateTransactionUseCaseImpl implements CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    @Override
    public Transaction createTransaction(TransactionRequest request, TransactionStatus status) {
        Transaction transaction = new Transaction();
        if (request instanceof TopUpWalletRequest topUpWalletRequest) {
            transaction = transactionMapper.toTransaction(topUpWalletRequest);
            transaction.setCreatedAt(new Date());
        }
        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }

}
