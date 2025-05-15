package com.playtomic.tests.wallet.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateWalletRequest {

    @Valid
    @NotBlank(message = "Alias is required")
    private String alias;

    @Valid
    @NotBlank(message = "UserId is required")
    private String userId;

}
