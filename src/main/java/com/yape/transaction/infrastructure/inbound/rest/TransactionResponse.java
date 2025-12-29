package com.yape.transaction.infrastructure.inbound.rest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionExternalId,
        TransactionTypeResponse transactionType,
        TransactionStatusResponse transactionStatus,
        BigDecimal value,
        Instant createdAt
) {
    public record TransactionTypeResponse(String name) {}
    public record TransactionStatusResponse(String name) {}
}
