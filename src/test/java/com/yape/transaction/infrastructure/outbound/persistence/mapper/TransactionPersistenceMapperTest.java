package com.yape.transaction.infrastructure.outbound.persistence.mapper;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import com.yape.transaction.infrastructure.outbound.persistence.TransactionEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionPersistenceMapperTest {

    private final TransactionPersistenceMapper mapper = new TransactionPersistenceMapper();

    @Test
    void returnNewEntityWhenTransactionIsPending() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.TRANSFER, new BigDecimal("10.00"),
                TransactionStatus.PENDING, Instant.now()
        );

        TransactionEntity entity = mapper.toEntity(transaction);

        assertThat(entity.isNew()).isTrue();
        assertThat(entity.getStatus()).isEqualTo("PENDING");
        assertThat(entity.getType()).isEqualTo("TRANSFER");
    }

    @Test
    void returnExistingEntityWhenTransactionIsNotPending() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.TRANSFER, new BigDecimal("10.00"),
                TransactionStatus.APPROVED, Instant.now()
        );

        TransactionEntity entity = mapper.toEntity(transaction);

        assertThat(entity.isNew()).isFalse();
        assertThat(entity.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void returnDomainMappedFromEntity() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-05T10:00:00Z");

        TransactionEntity entity = new TransactionEntity(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "TRANSFER",
                "REJECTED",
                new BigDecimal("99.90"),
                createdAt,
                false
        );

        Transaction domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(domain.getStatus()).isEqualTo(TransactionStatus.REJECTED);
        assertThat(domain.getValue()).isEqualByComparingTo("99.90");
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
    }
}
