package com.yape.transaction.application.dto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionView(
        UUID transactionExternalId,
        String transactionTypeName,
        String transactionStatusName,
        BigDecimal value,
        Instant createdAt
) {}