package com.playtomic.tests.wallet.presentation.dto;

import com.playtomic.tests.wallet.domain.enums.WalletStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    private String id;

    private String alias;

    private BigDecimal totalBalance;

    private BigDecimal availableBalance;

    private WalletStatus status;

    private Date createdAt;

    private Date updatedAt;

    private String userId;
}
