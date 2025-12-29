package com.yape.transaction.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("createPending: should create transaction with PENDING status and generated id/createdAt")
    void createPending_shouldCreatePendingTransaction() {
        // Given
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();
        BigDecimal value = new BigDecimal("10.00");

        // When
        Transaction tx = Transaction.createPending(debit, credit, TransactionType.TRANSFER, value);

        // Then
        assertThat(tx).isNotNull();
        assertThat(tx.getId()).isNotNull();
        assertThat(tx.getAccountExternalIdDebit()).isEqualTo(debit);
        assertThat(tx.getAccountExternalIdCredit()).isEqualTo(credit);
        assertThat(tx.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(tx.getValue()).isEqualByComparingTo(value);
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(tx.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("updateStatus: should throw IllegalArgumentException when newStatus is null")
    void updateStatus_shouldThrowWhenNull() {
        // Given
        Transaction tx = Transaction.createPending(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.TRANSFER,
                new BigDecimal("1.00")
        );

        // When
        Throwable thrown = catchThrowable(() -> tx.updateStatus(null));

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("status is required");
    }

    @Test
    @DisplayName("updateStatus: should update status when valid")
    void updateStatus_shouldUpdate() {
        // Given
        Transaction tx = Transaction.createPending(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.TRANSFER,
                new BigDecimal("1.00")
        );

        // When
        tx.updateStatus(TransactionStatus.APPROVED);

        // Then
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    }

    @Test
    @DisplayName("approve: should set status to APPROVED")
    void approve_shouldSetApproved() {
        // Given
        Transaction tx = Transaction.createPending(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.TRANSFER,
                new BigDecimal("1.00")
        );

        // When
        tx.approve();

        // Then
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.APPROVED);
    }

    @Test
    @DisplayName("reject: should set status to REJECTED")
    void reject_shouldSetRejected() {
        // Given
        Transaction tx = Transaction.createPending(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.TRANSFER,
                new BigDecimal("1.00")
        );

        // When
        tx.reject();

        // Then
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.REJECTED);
    }
}
