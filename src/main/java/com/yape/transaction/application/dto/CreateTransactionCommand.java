package com.yape.transaction.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionCommand(
        UUID accountExternalIdDebit,
        UUID accountExternalIdCredit,
        Integer tranferTypeId,
        BigDecimal value
) {}