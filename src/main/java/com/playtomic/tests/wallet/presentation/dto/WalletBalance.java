package com.playtomic.tests.wallet.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class WalletBalance {

    private BigDecimal totalBalance;

    private BigDecimal availableBalance;

}
