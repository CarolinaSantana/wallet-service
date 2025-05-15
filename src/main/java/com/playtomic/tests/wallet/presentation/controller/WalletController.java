package com.playtomic.tests.wallet.presentation.controller;

import com.playtomic.tests.wallet.application.usecase.CreateWalletUseCase;
import com.playtomic.tests.wallet.application.usecase.GetWalletUseCase;
import com.playtomic.tests.wallet.application.usecase.TopUpWalletUseCase;
import com.playtomic.tests.wallet.presentation.dto.CreateWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.TopUpWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final CreateWalletUseCase createWalletUseCase;
    private final GetWalletUseCase getWalletUseCase;
    private final TopUpWalletUseCase topUpWalletUseCase;

    public WalletController(CreateWalletUseCase createWalletUseCase,
                            GetWalletUseCase getWalletUseCase,
                            TopUpWalletUseCase topUpWalletUseCase) {
        this.createWalletUseCase = createWalletUseCase;
        this.getWalletUseCase = getWalletUseCase;
        this.topUpWalletUseCase = topUpWalletUseCase;
    }

    /**
     * Creates a new wallet based on the provided request data
     *
     * @param request The information to associate to the new wallet
     * @return The wallet information
     */
    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        WalletResponse response = createWalletUseCase.createWallet(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a wallet using its identifier
     *
     * @param id The wallet id
     * @return The wallet information
     */
    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable("id") String id) {
        WalletResponse response = getWalletUseCase.getWallet(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Top-up a wallet with a specific amount using a credit card.
     *
     * @param request The top-up request containing wallet ID, total, and credit card number
     * @return The updated wallet information
     */
    @PostMapping("/top-up")
    public ResponseEntity<WalletResponse> topUpWallet(@Valid @RequestBody TopUpWalletRequest request) {
        WalletResponse response = topUpWalletUseCase.topUpWallet(request);
        return ResponseEntity.ok(response);
    }

}
