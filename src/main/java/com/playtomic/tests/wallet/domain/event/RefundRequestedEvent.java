package com.playtomic.tests.wallet.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
public class RefundRequestedEvent extends ApplicationEvent {

    private final String operationId;

    private final String walletId;

    private final String reference;

    private final BigDecimal total;

    public RefundRequestedEvent(Object source, String walletId, String reference, BigDecimal total,
                                String operationId) {
        super(source);
        this.walletId = walletId;
        this.reference = reference;
        this.total = total;
        this.operationId = operationId;
    }

}
