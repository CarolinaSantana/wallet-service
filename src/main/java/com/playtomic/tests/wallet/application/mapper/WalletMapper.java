package com.playtomic.tests.wallet.application.mapper;

import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.presentation.dto.CreateWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    Wallet toWallet(CreateWalletRequest request);

    WalletResponse toWalletResponse(Wallet savedWallet);
}
