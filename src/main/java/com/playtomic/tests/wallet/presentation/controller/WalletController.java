package com.playtomic.tests.wallet.presentation.controller;

import com.playtomic.tests.wallet.application.usecase.CreateWalletUseCase;
import com.playtomic.tests.wallet.presentation.dto.CreateWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;

    public WalletController(CreateWalletUseCase createWalletUseCase) {
        this.createWalletUseCase = createWalletUseCase;
    }

    /**
     * Creates a new wallet based on the provided request data
     */
    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        WalletResponse response = createWalletUseCase.createWallet(request);
        return ResponseEntity.ok(response);
    }

}
