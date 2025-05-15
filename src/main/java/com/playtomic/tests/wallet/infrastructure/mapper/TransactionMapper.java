package com.playtomic.tests.wallet.infrastructure.mapper;

import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.presentation.dto.TransactionRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toTransaction(TransactionRequest request);

}
