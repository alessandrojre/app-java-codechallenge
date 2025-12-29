package com.yape.transaction.infrastructure.outbound.kafka;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCreatedEvent(
        UUID transactionExternalId,
        UUID accountExternalIdDebit,
        UUID accountExternalIdCredit,
        Integer tranferTypeId,
        BigDecimal value,
        Instant createdAt
) {}