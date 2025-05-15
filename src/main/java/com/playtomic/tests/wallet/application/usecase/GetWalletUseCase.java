package com.playtomic.tests.wallet.application.usecase;

import com.playtomic.tests.wallet.presentation.dto.WalletResponse;

public interface GetWalletUseCase {

    WalletResponse getWallet(String id);

}
