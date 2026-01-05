package com.yape.transaction.application.usecase;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UpdateTransactionStatusUseCase {
    Mono<Void> updateStatus(UUID transactionExternalId, String transactionNewStatusName);
}
