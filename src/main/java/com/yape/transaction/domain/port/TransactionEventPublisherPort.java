package com.yape.transaction.domain.port;

import com.yape.transaction.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface TransactionEventPublisherPort {
    Mono<Void> publishTransactionCreated(Transaction transaction);
}