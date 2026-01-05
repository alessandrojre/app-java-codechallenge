package com.yape.transaction.infrastructure.outbound.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Table("transactions")
@Getter
@Setter
@NoArgsConstructor
public class TransactionEntity implements Persistable<UUID> {

    @Id
    private UUID id;
    @Column("account_external_id_debit")
    private UUID accountExternalIdDebit;
    @Column("account_external_id_credit")
    private UUID accountExternalIdCredit;
    private String type;
    private String status;
    private BigDecimal value;
    @Column("created_at")
    private Instant createdAt;

    @Transient
    private boolean isNewRecord = false;

    public TransactionEntity(UUID id, UUID accountExternalIdDebit, UUID accountExternalIdCredit,
                             String type, String status, BigDecimal value,
                             Instant createdAt, boolean isNewRecord) {
        this.id = id;
        this.accountExternalIdDebit = accountExternalIdDebit;
        this.accountExternalIdCredit = accountExternalIdCredit;
        this.type = type;
        this.status = status;
        this.value = value;
        this.createdAt = createdAt;
        this.isNewRecord = isNewRecord;
    }

    @Override
    public boolean isNew() {
        return isNewRecord;
    }
}
