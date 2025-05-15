package com.playtomic.tests.wallet.infrastructure.persistence;

import com.playtomic.tests.wallet.domain.enums.WalletStatus;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.infrastructure.mapper.WalletMapper;
import com.playtomic.tests.wallet.infrastructure.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.WalletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletPersistenceService {

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public void updateWallet(Wallet wallet, BigDecimal totalBalance, BigDecimal availableBalance) {
        wallet.setTotalBalance(wallet.getTotalBalance().add(totalBalance));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(availableBalance));
        if (WalletStatus.PENDING.equals(wallet.getStatus())) {
            wallet.setStatus(WalletStatus.ACTIVATED);
        }
        wallet.setUpdatedAt(new Date());
        walletRepository.save(wallet);
    }

    @Recover
    public WalletResponse recoverFromFailure(Throwable ex, Wallet wallet, BigDecimal availableBalance, BigDecimal totalBalance) {
        log.error("Wallet update failed after retries. Marking Wallet {} as INCONSISTENT.", wallet.getId(), ex);
        try {
            wallet.setStatus(WalletStatus.INCONSISTENT);
            wallet.setUpdatedAt(new Date());
            walletRepository.save(wallet);
        } catch (Exception e) {
            log.error("Failed to persist INCONSISTENT status for Wallet {}.", wallet.getId(), e);
            //We can launch a new event as an alert to be treated with a defined procedure
        }
        return walletMapper.toWalletResponse(wallet);
    }


}
