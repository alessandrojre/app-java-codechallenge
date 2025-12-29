package com.yape.transaction.infrastructure.outbound.persistence;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.port.TransactionRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository jpaRepository;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = toEntity(transaction);
        TransactionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(UUID transactionId) {
        return jpaRepository.findById(transactionId)
                .map(this::toDomain);
    }

    private TransactionEntity toEntity(Transaction tx) {
        return new TransactionEntity(
                tx.getId(),
                tx.getAccountExternalIdDebit(),
                tx.getAccountExternalIdCredit(),
                tx.getType(),
                tx.getStatus(),
                tx.getValue(),
                tx.getCreatedAt()
        );
    }

    private Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getAccountExternalIdDebit(),
                entity.getAccountExternalIdCredit(),
                entity.getType(),
                entity.getValue(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
