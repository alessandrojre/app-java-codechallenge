package com.yape.transaction.infrastructure.outbound.persistence;

import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID accountExternalIdDebit;

    @Column(nullable = false)
    private UUID accountExternalIdCredit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private Instant createdAt;

    protected TransactionEntity() {
    }

    public TransactionEntity(UUID id,
                             UUID debit,
                             UUID credit,
                             TransactionType type,
                             TransactionStatus status,
                             BigDecimal value,
                             Instant createdAt) {
        this.id = id;
        this.accountExternalIdDebit = debit;
        this.accountExternalIdCredit = credit;
        this.type = type;
        this.status = status;
        this.value = value;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getAccountExternalIdDebit() { return accountExternalIdDebit; }
    public UUID getAccountExternalIdCredit() { return accountExternalIdCredit; }
    public TransactionType getType() { return type; }
    public TransactionStatus getStatus() { return status; }
    public BigDecimal getValue() { return value; }
    public Instant getCreatedAt() { return createdAt; }
}
