package com.playtomic.tests.wallet.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playtomic.tests.wallet.domain.enums.TransactionStatus;
import com.playtomic.tests.wallet.domain.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "transaction")
public class Transaction {

    @Id
    private String id;

    private BigDecimal total;

    private BigDecimal baseTotal;

    private TransactionType type;

    private TransactionStatus status;

    private String concept;

    private String reference;

    private String walletId;

    @Version
    private Integer version;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private OffsetDateTime updatedAt;

    public Transaction() {
        this.id = UUID.randomUUID().toString();
    }

}
