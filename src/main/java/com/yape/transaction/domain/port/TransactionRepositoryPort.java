package com.yape.transaction.domain.port;

import com.yape.transaction.domain.model.Transaction;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransactionRepositoryPort {

    Mono<Transaction> save(Transaction transaction);

    Mono<Transaction> findById(UUID transactionExternalId);
}