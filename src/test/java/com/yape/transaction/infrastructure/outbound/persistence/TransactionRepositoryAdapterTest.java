package com.yape.transaction.infrastructure.outbound.persistence;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransactionRepositoryAdapterTest {

    private TransactionJpaRepository jpaRepository;
    private TransactionRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        // Given
        jpaRepository = mock(TransactionJpaRepository.class);
        adapter = new TransactionRepositoryAdapter(jpaRepository);
    }

    @Test
    @DisplayName("save: should map domain to entity, persist, and map back to domain")
    void save_shouldMapPersistAndMapBack() {
        // Given
        UUID id = UUID.randomUUID();
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();
        BigDecimal value = new BigDecimal("10.00");
        Instant createdAt = Instant.parse("2025-12-29T00:00:00Z");

        Transaction domain = new Transaction(
                id, debit, credit,
                TransactionType.TRANSFER,
                value,
                TransactionStatus.PENDING,
                createdAt
        );

        TransactionEntity persisted = new TransactionEntity(
                id, debit, credit,
                TransactionType.TRANSFER,
                TransactionStatus.PENDING,
                value,
                createdAt
        );

        when(jpaRepository.save(any(TransactionEntity.class))).thenReturn(persisted);

        // When
        Transaction result = adapter.save(domain);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getAccountExternalIdDebit()).isEqualTo(debit);
        assertThat(result.getAccountExternalIdCredit()).isEqualTo(credit);
        assertThat(result.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(result.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(result.getValue()).isEqualByComparingTo(value);
        assertThat(result.getCreatedAt()).isEqualTo(createdAt);

        verify(jpaRepository).save(any(TransactionEntity.class));
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("findById: should return mapped domain when entity exists")
    void findById_shouldReturnDomainWhenExists() {
        // Given
        UUID id = UUID.randomUUID();
        TransactionEntity entity = new TransactionEntity(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.TRANSFER,
                TransactionStatus.APPROVED,
                new BigDecimal("20.00"),
                Instant.parse("2025-12-29T00:00:00Z")
        );

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        // When
        Optional<Transaction> result = adapter.findById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(entity.getId());
        assertThat(result.get().getType()).isEqualTo(entity.getType());
        assertThat(result.get().getStatus()).isEqualTo(entity.getStatus());
        assertThat(result.get().getValue()).isEqualByComparingTo(entity.getValue());
        assertThat(result.get().getCreatedAt()).isEqualTo(entity.getCreatedAt());

        verify(jpaRepository).findById(id);
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    @DisplayName("findById: should return empty when entity does not exist")
    void findById_shouldReturnEmptyWhenNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        // When
        Optional<Transaction> result = adapter.findById(id);

        // Then
        assertThat(result).isEmpty();
        verify(jpaRepository).findById(id);
        verifyNoMoreInteractions(jpaRepository);
    }
}
