package com.yape.transaction.infrastructure.inbound.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(
        @NotNull
        UUID transactionExternalId,

        @NotNull
        UUID accountExternalIdDebit,

        @NotNull
        UUID accountExternalIdCredit,

        @NotNull
        Integer transferTypeId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal value
) {}
