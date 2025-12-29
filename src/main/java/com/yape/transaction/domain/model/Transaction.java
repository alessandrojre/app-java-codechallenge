package com.yape.transaction.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transaction {

    private final UUID id;
    private final UUID accountExternalIdDebit;
    private final UUID accountExternalIdCredit;
    private final TransactionType type;
    private final BigDecimal value;
    private final Instant createdAt;

    private TransactionStatus status;

    public Transaction(UUID id,
                       UUID accountExternalIdDebit,
                       UUID accountExternalIdCredit,
                       TransactionType type,
                       BigDecimal value,
                       TransactionStatus status,
                       Instant createdAt) {
        this.id = id;
        this.accountExternalIdDebit = accountExternalIdDebit;
        this.accountExternalIdCredit = accountExternalIdCredit;
        this.type = type;
        this.value = value;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void updateStatus(TransactionStatus newStatus) {
        if (newStatus == null) throw new IllegalArgumentException("status is required");
        this.status = newStatus;
    }

    public static Transaction createPending(UUID debit, UUID credit, TransactionType type, BigDecimal value) {
        return new Transaction(
                UUID.randomUUID(),
                debit,
                credit,
                type,
                value,
                TransactionStatus.PENDING,
                Instant.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getAccountExternalIdDebit() { return accountExternalIdDebit; }
    public UUID getAccountExternalIdCredit() { return accountExternalIdCredit; }
    public TransactionType getType() { return type; }
    public BigDecimal getValue() { return value; }
    public TransactionStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void approve() { this.status = TransactionStatus.APPROVED; }
    public void reject() { this.status = TransactionStatus.REJECTED; }
}