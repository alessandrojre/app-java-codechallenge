package com.yape.transaction.application.service;

import com.yape.transaction.application.dto.CreateTransactionCommand;
import com.yape.transaction.application.dto.TransactionView;
import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import com.yape.transaction.domain.port.TransactionEventPublisherPort;
import com.yape.transaction.domain.port.TransactionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionRepositoryPort repository;
    private TransactionEventPublisherPort publisher;
    private TransactionService service;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionRepositoryPort.class);
        publisher = mock(TransactionEventPublisherPort.class);
        service = new TransactionService(repository, publisher);
    }

    @Test
    void create_shouldCreateSavePublishAndReturnId() {
        // Given
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();
        Integer transferTypeId = 1;
        BigDecimal value = new BigDecimal("10.50");

        CreateTransactionCommand command = new CreateTransactionCommand(
                debit, credit, transferTypeId, value
        );


        when(repository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<Transaction> txCaptor = ArgumentCaptor.forClass(Transaction.class);

        // When
        UUID result = service.create(command);

        // Then
        assertThat(result).isNotNull();

        verify(repository).save(txCaptor.capture());
        Transaction savedTx = txCaptor.getValue();

        assertThat(savedTx).isNotNull();
        assertThat(savedTx.getId()).isEqualTo(result);
        assertThat(savedTx.getAccountExternalIdDebit()).isEqualTo(debit);
        assertThat(savedTx.getAccountExternalIdCredit()).isEqualTo(credit);
        assertThat(savedTx.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(savedTx.getValue()).isEqualByComparingTo(value);
        assertThat(savedTx.getStatus()).isEqualTo(TransactionStatus.PENDING);
        assertThat(savedTx.getCreatedAt()).isNotNull();

        verify(publisher).publishTransactionCreated(savedTx);
        verifyNoMoreInteractions(publisher);
    }

    @Test
    @DisplayName("create: should throw IllegalArgumentException when tranferTypeId is null")
    void create_shouldThrowWhenTransferTypeIdIsNull() {
        // Given
        CreateTransactionCommand command = new CreateTransactionCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                new BigDecimal("1.00")
        );

        // When
        Throwable thrown = catchThrowable(() -> service.create(command));

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tranferTypeId is required");

        verifyNoInteractions(repository);
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("create: should throw IllegalArgumentException when tranferTypeId is unsupported")
    void create_shouldThrowWhenTransferTypeIdUnsupported() {
        // Given
        CreateTransactionCommand command = new CreateTransactionCommand(
                UUID.randomUUID(),
                UUID.randomUUID(),
                4,
                new BigDecimal("1.00")
        );

        // When
        Throwable thrown = catchThrowable(() -> service.create(command));

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported tranferTypeId: 4");

        verifyNoInteractions(repository);
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("getById: should return TransactionView when transaction exists")
    void getById_shouldReturnViewWhenExists() {
        // Given
        UUID id = UUID.randomUUID();
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();
        BigDecimal value = new BigDecimal("20.00");
        Instant createdAt = Instant.parse("2025-12-29T00:00:00Z");

        Transaction tx = new Transaction(
                id,
                debit,
                credit,
                TransactionType.TRANSFER,
                value,
                TransactionStatus.APPROVED,
                createdAt
        );

        when(repository.findById(id)).thenReturn(Optional.of(tx));

        // When
        TransactionView view = service.getById(id);

        // Then
        assertThat(view).isNotNull();
        assertThat(view.transactionExternalId()).isEqualTo(id);
        assertThat(view.transactionTypeName()).isEqualTo(TransactionType.TRANSFER.name());
        assertThat(view.transactionStatusName()).isEqualTo(TransactionStatus.APPROVED.name());
        assertThat(view.value()).isEqualByComparingTo(value);
        assertThat(view.createdAt()).isEqualTo(createdAt);

        verify(repository).findById(id);
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("getById: should throw IllegalArgumentException when transaction does not exist")
    void getById_shouldThrowWhenNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> service.getById(id));

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction not found: " + id);

        verify(repository).findById(id);
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("updateStatus: should update status and save transaction when found (case-insensitive)")
    void updateStatus_shouldUpdateAndSaveWhenFound() {
        // Given
        UUID id = UUID.randomUUID();

        Transaction tx = new Transaction(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.TRANSFER,
                new BigDecimal("5.00"),
                TransactionStatus.PENDING,
                Instant.parse("2025-12-29T00:00:00Z")
        );

        when(repository.findById(id)).thenReturn(Optional.of(tx));
        when(repository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        service.updateStatus(id, "approved"); // minúsculas para validar upperCase()

        // Then
        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.APPROVED);

        verify(repository).findById(id);
        verify(repository).save(tx);
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("updateStatus: should throw IllegalArgumentException when transaction does not exist")
    void updateStatus_shouldThrowWhenNotFound() {
        // Given
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> service.updateStatus(id, "APPROVED"));

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaction not found: " + id);

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(publisher);
    }

    @Test
    @DisplayName("updateStatus:  throw IllegalArgumentException when newStatus is invalid")
    void updateStatus_shouldThrowWhenStatusInvalid() {
        // Given
        UUID id = UUID.randomUUID();

        Transaction tx = new Transaction(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.TRANSFER,
                new BigDecimal("5.00"),
                TransactionStatus.PENDING,
                Instant.parse("2025-12-29T00:00:00Z")
        );

        when(repository.findById(id)).thenReturn(Optional.of(tx));

        // When
        Throwable thrown = catchThrowable(() -> service.updateStatus(id, "NOT_A_REAL_STATUS"));

        // Then
        assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class);


        verify(repository).findById(id);
        verify(repository, never()).save(any(Transaction.class));
        verifyNoInteractions(publisher);
    }
}
