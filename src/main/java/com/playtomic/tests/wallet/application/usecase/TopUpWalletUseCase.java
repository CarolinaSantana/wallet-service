package com.playtomic.tests.wallet.application.usecase;

import com.playtomic.tests.wallet.presentation.dto.TopUpWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;

public interface TopUpWalletUseCase {

    WalletResponse topUpWallet(TopUpWalletRequest request);

}
