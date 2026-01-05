package com.yape.transaction.infrastructure.outbound.persistence;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.port.TransactionRepositoryPort;
import com.yape.transaction.infrastructure.outbound.persistence.mapper.TransactionPersistenceMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionR2dbcRepository transactionR2dbcRepository;
    private final TransactionPersistenceMapper mapper;

    public TransactionRepositoryAdapter(TransactionR2dbcRepository transactionR2dbcRepository,
                                        TransactionPersistenceMapper mapper) {
        this.transactionR2dbcRepository = transactionR2dbcRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Transaction> save(Transaction transaction) {
        TransactionEntity entity = mapper.toEntity(transaction);

        return transactionR2dbcRepository.save(entity)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Transaction> findById(UUID transactionExternalId) {
        return transactionR2dbcRepository.findById(transactionExternalId)
                .map(mapper::toDomain);
    }
}