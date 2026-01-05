package com.yape.transaction.infrastructure.outbound.kafka;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class TransactionCreatedEvent {

    private UUID transactionExternalId;
    private UUID accountExternalIdDebit;
    private UUID accountExternalIdCredit;
    private Integer transferTypeId;
    private BigDecimal value;
    private Instant createdAt;

    public TransactionCreatedEvent() {
    }

    public TransactionCreatedEvent(
            UUID transactionExternalId,
            UUID accountExternalIdDebit,
            UUID accountExternalIdCredit,
            Integer transferTypeId,
            BigDecimal value,
            Instant createdAt
    ) {
        this.transactionExternalId = transactionExternalId;
        this.accountExternalIdDebit = accountExternalIdDebit;
        this.accountExternalIdCredit = accountExternalIdCredit;
        this.transferTypeId = transferTypeId;
        this.value = value;
        this.createdAt = createdAt;
    }

}
