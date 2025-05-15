package com.playtomic.tests.wallet.domain.event;

import com.playtomic.tests.wallet.domain.model.Transaction;
import com.playtomic.tests.wallet.domain.model.Wallet;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TopUpRequestedEvent extends ApplicationEvent {

    private final String operationId;

    private final transient Transaction transaction;

    private final transient Wallet wallet;

    private final String creditCardNumber;

    public TopUpRequestedEvent(Object source, Transaction transaction, Wallet wallet, String creditCardNumber,
                               String operationId) {
        super(source);
        this.transaction = transaction;
        this.wallet = wallet;
        this.creditCardNumber = creditCardNumber;
        this.operationId = operationId;
    }

}
