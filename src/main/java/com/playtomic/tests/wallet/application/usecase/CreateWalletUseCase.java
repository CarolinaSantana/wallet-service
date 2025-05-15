package com.playtomic.tests.wallet.application.usecase;

import com.playtomic.tests.wallet.presentation.dto.CreateWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;

public interface CreateWalletUseCase {

    WalletResponse createWallet(CreateWalletRequest createWalletRequest);

}
