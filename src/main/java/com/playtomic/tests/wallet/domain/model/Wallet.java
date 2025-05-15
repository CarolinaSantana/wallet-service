package com.playtomic.tests.wallet.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playtomic.tests.wallet.domain.enums.WalletStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "wallet")
public class Wallet {

    @Id
    private String id;

    private String alias;

    private BigDecimal totalBalance;

    private BigDecimal availableBalance;

    private WalletStatus status;

    @Version
    Long version;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date updatedAt;

    private String userId;

    public Wallet() {
        this.id = UUID.randomUUID().toString();
    }
}
