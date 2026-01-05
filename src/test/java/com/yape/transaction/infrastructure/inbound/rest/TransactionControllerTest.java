package com.yape.transaction.infrastructure.inbound.rest.controller;

import com.yape.transaction.application.dto.TransactionView;
import com.yape.transaction.application.usecase.CreateTransactionUseCase;
import com.yape.transaction.application.usecase.GetTransactionUseCase;
import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import com.yape.transaction.infrastructure.inbound.rest.mapper.TransactionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = TransactionController.class)
@Import({TransactionMapper.class})
class TransactionControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    CreateTransactionUseCase createTransactionUseCase;

    @MockBean
    GetTransactionUseCase getTransactionUseCase;

    @Test
    void returnCreatedAndBody() {
        UUID txId = UUID.randomUUID();
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();

        Transaction saved = new Transaction(
                txId, debit, credit,
                TransactionType.TRANSFER,
                new BigDecimal("12.34"),
                TransactionStatus.PENDING,
                Instant.parse("2026-01-05T10:00:00Z")
        );

        when(createTransactionUseCase.create(any())).thenReturn(Mono.just(saved));

        String body = """
                {
                  "transactionExternalId": "%s",
                  "accountExternalIdDebit": "%s",
                  "accountExternalIdCredit": "%s",
                  "transferTypeId": 1,
                  "value": 12.34
                }
                """.formatted(txId, debit, credit);

        webTestClient.post()
                .uri("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.transactionExternalId").isEqualTo(txId.toString())
                .jsonPath("$.transactionType.name").isEqualTo("TRANSFER")
                .jsonPath("$.transactionStatus.name").isEqualTo("PENDING")
                .jsonPath("$.value").isEqualTo(12.34);

        verify(createTransactionUseCase, times(1)).create(any());
    }

    @Test
    void returnStatusBadRequestValueInvalid() {
        UUID txId = UUID.randomUUID();
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();

        String body = """
                {
                  "transactionExternalId": "%s",
                  "accountExternalIdDebit": "%s",
                  "accountExternalIdCredit": "%s",
                  "transferTypeId": 1,
                  "value": 0.00
                }
                """.formatted(txId, debit, credit);

        webTestClient.post()
                .uri("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();

        verifyNoInteractions(createTransactionUseCase);
    }

    @Test
    void returnTransactionById() {
        UUID id = UUID.randomUUID();
        TransactionView view = new TransactionView(
                id,
                "TRANSFER",
                "APPROVED",
                new BigDecimal("99.99"),
                Instant.parse("2026-01-05T10:00:00Z")
        );

        when(getTransactionUseCase.getById(id)).thenReturn(Mono.just(view));

        webTestClient.get()
                .uri("/transactions/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.transactionExternalId").isEqualTo(id.toString())
                .jsonPath("$.transactionType.name").isEqualTo("TRANSFER")
                .jsonPath("$.transactionStatus.name").isEqualTo("APPROVED")
                .jsonPath("$.value").isEqualTo(99.99);

        verify(getTransactionUseCase, times(1)).getById(id);
    }
}
