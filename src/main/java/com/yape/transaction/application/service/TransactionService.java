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

import java.util.UUID;

public class TransactionService implements UpdateTransactionStatusUseCase,
        CreateTransactionUseCase, GetTransactionUseCase {

    private final TransactionRepositoryPort repository;
    private final TransactionEventPublisherPort publisher;

    public TransactionService(TransactionRepositoryPort repository,
                              TransactionEventPublisherPort publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public UUID create(CreateTransactionCommand command) {

        TransactionType type = mapType(command.tranferTypeId());

        Transaction transaction = Transaction.createPending(
                command.accountExternalIdDebit(),
                command.accountExternalIdCredit(),
                type,
                command.value()
        );

        repository.save(transaction);
        publisher.publishTransactionCreated(transaction);

        return transaction.getId();
    }

    @Override
    public TransactionView getById(UUID transactionExternalId) {
        Transaction transaction = repository.findById(transactionExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionExternalId));

        return new TransactionView(
                transaction.getId(),
                transaction.getType().name(),
                transaction.getStatus().name(),
                transaction.getValue(),
                transaction.getCreatedAt()
        );
    }

    @Override
    public void updateStatus(UUID transactionExternalId, String newStatus) {

        var transaction = repository.findById(transactionExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionExternalId));

        var status = TransactionStatus.valueOf(newStatus.toUpperCase());

        transaction.updateStatus(status);
        repository.save(transaction);
    }

    private TransactionType mapType(Integer tranferTypeId) {
        if (tranferTypeId == null) {
            throw new IllegalArgumentException("tranferTypeId is required");
        }
        if (tranferTypeId == 1) {
            return TransactionType.TRANSFER;
        }
        throw new IllegalArgumentException("Unsupported tranferTypeId: " + tranferTypeId);
    }
}
