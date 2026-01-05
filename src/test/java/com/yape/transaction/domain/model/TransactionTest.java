package com.yape.transaction.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class TransactionTest {

    @Test
    void throwErrorWhenStatusIsNull() {
        Transaction transaction = Transaction.createPending(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.TRANSFER, new BigDecimal("1.00")
        );

        assertThatThrownBy(() -> transaction.updateStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("status is required");
    }

    @Test
    void returnApprovedStatusWhenApproveIsCalled() {
        Transaction transaction = Transaction.createPending(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.TRANSFER, new BigDecimal("1.00")
        );

        transaction.approve();

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    }

    @Test
    void returnRejectedStatusWhenRejectIsCalled() {
        Transaction transaction = Transaction.createPending(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.TRANSFER, new BigDecimal("1.00")
        );

        transaction.reject();

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.REJECTED);
    }
}
