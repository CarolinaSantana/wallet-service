package com.playtomic.tests.wallet.presentation.dto;

import com.playtomic.tests.wallet.domain.enums.TransactionType;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopUpWalletRequest extends TransactionRequest {

    @Valid
    private CreditCardNumber creditCardNumber;

    private String operationId;

    public TopUpWalletRequest() {
        this.setType(TransactionType.TOP_UP);
    }

}