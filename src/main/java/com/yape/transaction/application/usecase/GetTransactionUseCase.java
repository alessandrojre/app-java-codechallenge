package com.yape.transaction.application.usecase;

import com.yape.transaction.application.dto.TransactionView;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GetTransactionUseCase {
    Mono<TransactionView> getById(UUID transactionExternalId);
}