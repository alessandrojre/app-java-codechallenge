package com.yape.transaction.infrastructure.outbound.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface TransactionR2dbcRepository extends ReactiveCrudRepository<TransactionEntity, UUID> {
}
