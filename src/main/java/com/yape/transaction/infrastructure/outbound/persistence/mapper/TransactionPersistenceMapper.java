package com.yape.transaction.infrastructure.outbound.persistence.mapper;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import com.yape.transaction.infrastructure.outbound.persistence.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionPersistenceMapper {

    public TransactionEntity toEntity(Transaction domain) {
        return new TransactionEntity(
                domain.getId(),
                domain.getAccountExternalIdDebit(),
                domain.getAccountExternalIdCredit(),
                domain.getType().name(),
                domain.getStatus().name(),
                domain.getValue(),
                domain.getCreatedAt(),
                domain.getStatus() == TransactionStatus.PENDING
        );
    }

    public Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getAccountExternalIdDebit(),
                entity.getAccountExternalIdCredit(),
                TransactionType.valueOf(entity.getType()),
                entity.getValue(),
                TransactionStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }
}