package com.yape.transaction.application.service;

import com.yape.transaction.application.dto.CreateTransactionCommand;
import com.yape.transaction.application.dto.TransactionView;
import com.yape.transaction.application.usecase.CreateTransactionUseCase;
import com.yape.transaction.application.usecase.GetTransactionUseCase;
import com.yape.transaction.application.usecase.UpdateTransactionStatusUseCase;
import com.yape.transaction.domain.model.Transaction;
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

        Transaction tx = Transaction.createPending(
                command.accountExternalIdDebit(),
                command.accountExternalIdCredit(),
                type,
                command.value()
        );

        repository.save(tx);
        publisher.publishTransactionCreated(tx);

        return tx.getId();
    }

    @Override
    public TransactionView getById(UUID transactionExternalId) {
        Transaction tx = repository.findById(transactionExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionExternalId));

        return new TransactionView(
                tx.getId(),
                tx.getType().name(),
                tx.getStatus().name(),
                tx.getValue(),
                tx.getCreatedAt()
        );
    }

    @Override
    public void updateStatus(UUID transactionExternalId, String newStatus) {

        var tx = repository.findById(transactionExternalId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionExternalId));

        var status = com.yape.transaction.domain.model.TransactionStatus.valueOf(newStatus.toUpperCase());

        tx.updateStatus(status);
        repository.save(tx);
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
