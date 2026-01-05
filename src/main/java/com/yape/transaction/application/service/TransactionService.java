package com.yape.transaction.application.service;

import com.yape.transaction.application.dto.CreateTransactionCommand;
import com.yape.transaction.application.dto.TransactionView;
import com.yape.transaction.application.usecase.CreateTransactionUseCase;
import com.yape.transaction.application.usecase.GetTransactionUseCase;
import com.yape.transaction.application.usecase.UpdateTransactionStatusUseCase;
import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import com.yape.transaction.domain.port.TransactionEventPublisherPort;
import com.yape.transaction.domain.port.TransactionRepositoryPort;
import com.yape.transaction.infrastructure.inbound.rest.exception.BusinessException;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class TransactionService implements CreateTransactionUseCase,
        GetTransactionUseCase,
        UpdateTransactionStatusUseCase {

    private final TransactionRepositoryPort transactionRepository;
    private final TransactionEventPublisherPort transactionEventPublisher;

    public TransactionService(TransactionRepositoryPort transactionRepository,
                              TransactionEventPublisherPort transactionEventPublisher) {
        this.transactionRepository = transactionRepository;
        this.transactionEventPublisher = transactionEventPublisher;
    }

    @Override
    public Mono<Transaction> create(CreateTransactionCommand command) {
        TransactionType transactionType = TransactionType.fromId(command.transferTypeId());

        UUID newTransactionId = UUID.randomUUID();

        Transaction transaction = Transaction.createPending(
                newTransactionId,
                command.accountExternalIdDebit(),
                command.accountExternalIdCredit(),
                transactionType,
                command.value()
        );

        return transactionRepository.save(transaction)
                .flatMap(savedTransaction ->
                        transactionEventPublisher.publishTransactionCreated(savedTransaction)
                                .thenReturn(savedTransaction)
                );
    }

    @Override
    public Mono<TransactionView> getById(UUID transactionExternalId) {
        return transactionRepository.findById(transactionExternalId)
                .map(transaction -> new TransactionView(
                        transaction.getId(),
                        transaction.getType().name(),
                        transaction.getStatus().name(),
                        transaction.getValue(),
                        transaction.getCreatedAt()
                ))
                .switchIfEmpty(Mono.error(new BusinessException("Transaction not found: " + transactionExternalId)));
    }

    @Override
    public Mono<Void> updateStatus(UUID transactionExternalId, String newStatusName) {
        return transactionRepository.findById(transactionExternalId)
                .flatMap(transaction -> {
                    TransactionStatus newStatus = TransactionStatus.valueOf(newStatusName.toUpperCase());

                    if (transaction.getStatus() != TransactionStatus.PENDING) {
                        return Mono.empty();
                    }

                    transaction.updateStatus(newStatus);
                    return transactionRepository.save(transaction);
                })
                .then();
    }
}