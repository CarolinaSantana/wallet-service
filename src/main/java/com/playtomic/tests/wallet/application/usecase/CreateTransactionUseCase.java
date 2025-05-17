package com.playtomic.tests.wallet.application.usecase;

import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.presentation.dto.TransactionRequest;

public interface CreateTransactionUseCase {

    Transaction createTransaction(TransactionRequest request, TransactionStatus status);

}
