package com.yape.transaction.application.service;

import com.yape.transaction.application.dto.CreateTransactionCommand;
import com.yape.transaction.application.dto.TransactionView;
import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import com.yape.transaction.domain.port.TransactionEventPublisherPort;
import com.yape.transaction.domain.port.TransactionRepositoryPort;
import com.yape.transaction.infrastructure.inbound.rest.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    TransactionRepositoryPort transactionRepository;

    @Mock
    TransactionEventPublisherPort transactionEventPublisher;

    @InjectMocks
    TransactionService service;

    @Captor
    ArgumentCaptor<Transaction> transactionCaptor;

    @BeforeEach
    void setUp() {

    }

    @Test
    void returnCreatedTransactionAndPublishEvent() {
        // given
        UUID externalTransactionIdFromRequest = UUID.randomUUID();
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();

        CreateTransactionCommand command = new CreateTransactionCommand(
                externalTransactionIdFromRequest,
                debit,
                credit,
                1,
                new BigDecimal("25.50")
        );

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        when(transactionEventPublisher.publishTransactionCreated(any(Transaction.class)))
                .thenReturn(Mono.empty());

        // act
        Mono<Transaction> result = service.create(command);

        // then
        StepVerifier.create(result)
                .assertNext(saved -> {
                    assertThat(saved.getId()).isNotNull();
                    assertThat(saved.getAccountExternalIdDebit()).isEqualTo(debit);
                    assertThat(saved.getAccountExternalIdCredit()).isEqualTo(credit);
                    assertThat(saved.getType()).isEqualTo(TransactionType.TRANSFER);
                    assertThat(saved.getValue()).isEqualByComparingTo("25.50");
                    assertThat(saved.getStatus()).isEqualTo(TransactionStatus.PENDING);
                    assertThat(saved.getCreatedAt()).isNotNull();
                })
                .verifyComplete();

        verify(transactionRepository, times(1)).save(transactionCaptor.capture());
        verify(transactionEventPublisher, times(1)).publishTransactionCreated(any(Transaction.class));



        assertThat(transactionCaptor.getValue().getId()).isNotEqualTo(externalTransactionIdFromRequest);
    }

    @Test
    void returnTransactionViewWhenTransactionExists() {
        // given
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                TransactionType.TRANSFER,
                new BigDecimal("10.00"),
                TransactionStatus.APPROVED,
                Instant.parse("2026-01-05T10:00:00Z")
        );

        when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));

        // act
        Mono<TransactionView> result = service.getById(id);

        // then
        StepVerifier.create(result)
                .assertNext(view -> {
                    assertThat(view.transactionExternalId()).isEqualTo(id);
                    assertThat(view.transactionTypeName()).isEqualTo("TRANSFER");
                    assertThat(view.transactionStatusName()).isEqualTo("APPROVED");
                    assertThat(view.value()).isEqualByComparingTo("10.00");
                    assertThat(view.createdAt()).isEqualTo(Instant.parse("2026-01-05T10:00:00Z"));
                })
                .verifyComplete();

        verify(transactionRepository).findById(id);
    }

    @Test
    void throwBusinessErrorWhenTransactionNotFound() {
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Mono.empty());

        Mono<TransactionView> result = service.getById(id);

        StepVerifier.create(result)
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(BusinessException.class);
                    assertThat(err.getMessage()).contains("Transaction not found: " + id);
                })
                .verify();

        verify(transactionRepository).findById(id);
    }

    @Test
    void returnUpdatedStatusWhenTransactionIsPending() {
        // given
        UUID id = UUID.randomUUID();
        Transaction transaction = Transaction.createPending(
                id, UUID.randomUUID(), UUID.randomUUID(), TransactionType.TRANSFER, new BigDecimal("1.00")
        );

        when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // act
        Mono<Void> result = service.updateStatus(id, "APPROVED");

        // then
        StepVerifier.create(result)
                .verifyComplete();

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
        verify(transactionRepository).findById(id);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void returnNothingWhenTransactionIsNotPending() {
        // given
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction(
                id, UUID.randomUUID(), UUID.randomUUID(),
                TransactionType.TRANSFER, new BigDecimal("2.00"),
                TransactionStatus.APPROVED, Instant.now()
        );

        when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));

        // act
        Mono<Void> result = service.updateStatus(id, "REJECTED");

        // then
        StepVerifier.create(result)
                .verifyComplete();

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.APPROVED);
        verify(transactionRepository).findById(id);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void throwErrorWhenStatusNameIsInvalid() {
        // given
        UUID id = UUID.randomUUID();
        Transaction transaction = Transaction.createPending(
                id, UUID.randomUUID(), UUID.randomUUID(), TransactionType.TRANSFER, new BigDecimal("3.00")
        );

        when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));

        // act
        Mono<Void> result = service.updateStatus(id, "NOT_A_STATUS");

        // then
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(transactionRepository).findById(id);
        verify(transactionRepository, never()).save(any());
    }
}
