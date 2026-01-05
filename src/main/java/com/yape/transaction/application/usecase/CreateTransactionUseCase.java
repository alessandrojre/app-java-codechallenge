package com.yape.transaction.application.usecase;

import com.yape.transaction.application.dto.CreateTransactionCommand;
import com.yape.transaction.domain.model.Transaction;
import reactor.core.publisher.Mono;

public interface CreateTransactionUseCase {
    Mono<Transaction> create(CreateTransactionCommand createTransactionCommand);
}