package com.yape.transaction.infrastructure.inbound.kafka;

import java.util.UUID;

public record TransactionStatusUpdatedEvent(
        UUID transactionExternalId,
        String status
) {}