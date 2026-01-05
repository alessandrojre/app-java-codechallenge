package com.yape.transaction.infrastructure.outbound.persistence;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import com.yape.transaction.infrastructure.outbound.persistence.mapper.TransactionPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryAdapterTest {

    @Mock
    TransactionR2dbcRepository r2dbcRepository;

    private TransactionRepositoryAdapter adapter;

    private TransactionPersistenceMapper mapper;

    @Captor
    ArgumentCaptor<TransactionEntity> entityCaptor;

    @BeforeEach
    void setUp() {
        mapper = new TransactionPersistenceMapper();
        adapter = new TransactionRepositoryAdapter(r2dbcRepository, mapper);
    }

    @Test
    void returnSavedTransactionWhenSaveIsCalled() {
        Transaction transaction = new Transaction(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.TRANSFER, new BigDecimal("10.00"),
                TransactionStatus.PENDING, Instant.now()
        );

        when(r2dbcRepository.save(any(TransactionEntity.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        Mono<Transaction> result = adapter.save(transaction);

        StepVerifier.create(result)
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo(transaction.getId());
                    assertThat(saved.getStatus()).isEqualTo(TransactionStatus.PENDING);
                    assertThat(saved.getType()).isEqualTo(TransactionType.TRANSFER);
                })
                .verifyComplete();

        verify(r2dbcRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getStatus()).isEqualTo("PENDING");
        assertThat(entityCaptor.getValue().getType()).isEqualTo("TRANSFER");
    }

    @Test
    void returnTransactionWhenFindByIdExists() {
        UUID id = UUID.randomUUID();

        TransactionEntity entity = new TransactionEntity(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "TRANSFER",
                "APPROVED",
                new BigDecimal("5.00"),
                Instant.now(),
                false
        );

        when(r2dbcRepository.findById(id)).thenReturn(Mono.just(entity));

        Mono<Transaction> result = adapter.findById(id);

        StepVerifier.create(result)
                .assertNext(found -> {
                    assertThat(found.getId()).isEqualTo(id);
                    assertThat(found.getType()).isEqualTo(TransactionType.TRANSFER);
                    assertThat(found.getStatus()).isEqualTo(TransactionStatus.APPROVED);
                })
                .verifyComplete();

        verify(r2dbcRepository).findById(id);
    }
}
