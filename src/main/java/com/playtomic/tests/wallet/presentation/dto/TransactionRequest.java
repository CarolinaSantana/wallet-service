package com.playtomic.tests.wallet.presentation.dto;

import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public abstract class TransactionRequest {

    @Positive(message = "Amount must be greater than 0")
    private BigDecimal total;

    private TransactionType type;

    private TransactionStatus status;

    private String concept;

    private String reference;

    @NotNull
    private String walletId;

    private Date createdAt;

    private String reason;

}
