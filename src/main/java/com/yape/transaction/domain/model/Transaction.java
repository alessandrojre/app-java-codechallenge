package com.yape.transaction.domain.model;

import lombok.Getter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Transaction {
    private final UUID id;
    private final UUID accountExternalIdDebit;
    private final UUID accountExternalIdCredit;
    private final TransactionType type;
    private final BigDecimal value;
    private final Instant createdAt;
    private TransactionStatus status;

    public Transaction(UUID id, UUID debit, UUID credit, TransactionType type,
                       BigDecimal value, TransactionStatus status, Instant createdAt) {
        this.id = id;
        this.accountExternalIdDebit = debit;
        this.accountExternalIdCredit = credit;
        this.type = type;
        this.value = value;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void updateStatus(TransactionStatus newStatus) {
        if (newStatus == null) throw new IllegalArgumentException("status is required");
        this.status = newStatus;
    }

    public static Transaction createPending(UUID transactionId, UUID debit, UUID credit,
                                            TransactionType type, BigDecimal value) {
        return new Transaction(transactionId, debit, credit, type, value,
                TransactionStatus.PENDING, Instant.now());
    }

    public void approve() { this.status = TransactionStatus.APPROVED; }
    public void reject() { this.status = TransactionStatus.REJECTED; }
}