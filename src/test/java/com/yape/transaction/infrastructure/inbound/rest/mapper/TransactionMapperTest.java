package com.yape.transaction.infrastructure.inbound.rest.mapper;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import com.yape.transaction.infrastructure.inbound.rest.dto.TransactionResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionMapperTest {

    private final TransactionMapper mapper = new TransactionMapper();

    @Test
    void returnResponseMappedFromTransaction() {
        // given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-05T10:00:00Z");

        Transaction transaction = new Transaction(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.TRANSFER,
                new BigDecimal("20.00"),
                TransactionStatus.PENDING,
                createdAt
        );

        // when
        TransactionResponse response = mapper.toResponse(transaction);

        // then
        assertThat(response.transactionExternalId()).isEqualTo(id);
        assertThat(response.transactionType().name()).isEqualTo("TRANSFER");
        assertThat(response.transactionStatus().name()).isEqualTo("PENDING");
        assertThat(response.value()).isEqualByComparingTo("20.00");
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }
}
