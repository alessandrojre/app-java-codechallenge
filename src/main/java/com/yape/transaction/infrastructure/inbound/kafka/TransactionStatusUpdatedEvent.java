package com.yape.transaction.infrastructure.inbound.kafka;

import lombok.Getter;

import java.util.UUID;

@Getter
public class TransactionStatusUpdatedEvent {

    private UUID transactionExternalId;
    private String status;

    public TransactionStatusUpdatedEvent() {
    }

    public TransactionStatusUpdatedEvent(UUID transactionExternalId, String status) {
        this.transactionExternalId = transactionExternalId;
        this.status = status;
    }

}
