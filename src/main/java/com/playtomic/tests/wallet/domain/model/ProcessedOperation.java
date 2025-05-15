package com.playtomic.tests.wallet.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "processedOperation")
public class ProcessedOperation {

    @Id
    private String id;

    private String walletId;

    public ProcessedOperation(String walletId, String operationId) {
        this.walletId = walletId;
        this.id = operationId;
    }

}
