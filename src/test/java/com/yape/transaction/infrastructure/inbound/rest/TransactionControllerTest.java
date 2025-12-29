package com.yape.transaction.infrastructure.inbound.rest;

import com.yape.transaction.application.dto.TransactionView;
import com.yape.transaction.application.usecase.CreateTransactionUseCase;
import com.yape.transaction.application.usecase.GetTransactionUseCase;
import com.yape.transaction.infrastructure.inbound.rest.exception.ApiExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class)
@Import({ApiExceptionHandler.class, TransactionControllerTest.TestConfig.class})
class TransactionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @Qualifier("createTransactionUseCase")
    private CreateTransactionUseCase createUseCase;

    @Autowired
    @Qualifier("getTransactionUseCase")
    private GetTransactionUseCase getUseCase;


    @TestConfiguration
    static class TestConfig {

        @Bean("createTransactionUseCase")
        CreateTransactionUseCase createTransactionUseCase() {
            return Mockito.mock(CreateTransactionUseCase.class);
        }

        @Bean("getTransactionUseCase")
        GetTransactionUseCase getTransactionUseCase() {
            return Mockito.mock(GetTransactionUseCase.class);
        }
    }

    @BeforeEach
    public void resetMocks() {
        reset(createUseCase, getUseCase);
    }


    @Test
    @DisplayName("POST /transactions: created")
    void create_shouldReturn201WhenValid() throws Exception {
        // Given
        UUID txId = UUID.randomUUID();
        when(createUseCase.create(any())).thenReturn(txId);

        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();

        String json = """
                {
                  "accountExternalIdDebit": "%s",
                  "accountExternalIdCredit": "%s",
                  "tranferTypeId": 1,
                  "value": 10.50
                }
                """.formatted(debit, credit);

        // When / Then
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionExternalId").value(txId.toString()));

        verify(createUseCase).create(any());
        verifyNoMoreInteractions(createUseCase);
        verifyNoInteractions(getUseCase);
    }

    @Test
    @DisplayName("POST /transactions: bad request")
    void create_shouldReturn400WhenInvalid() throws Exception {
        // Given
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();

        String json = """
                {
                  "accountExternalIdDebit": "%s",
                  "accountExternalIdCredit": "%s",
                  "tranferTypeId": 1,
                  "value": 0.00
                }
                """.formatted(debit, credit);

        // When
        // Then
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Solicitud inválida"));

        verifyNoInteractions(createUseCase);
        verifyNoInteractions(getUseCase);
    }

    @Test
    @DisplayName("GET /transactions/{id}: success")
    void get_shouldReturn200WhenFound() throws Exception {
        // Given
        UUID id = UUID.randomUUID();

        TransactionView view = new TransactionView(
                id,
                "TRANSFER",
                "APPROVED",
                new BigDecimal("25.00"),
                Instant.parse("2025-12-29T00:00:00Z")
        );

        when(getUseCase.getById(eq(id))).thenReturn(view);

        // When
        // Then
        mockMvc.perform(get("/transactions/{transactionExternalId}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.transactionExternalId").value(id.toString()))
                .andExpect(jsonPath("$.transactionType.name").value("TRANSFER"))
                .andExpect(jsonPath("$.transactionStatus.name").value("APPROVED"))
                .andExpect(jsonPath("$.value").value(25.00))
                .andExpect(jsonPath("$.createdAt").value("2025-12-29T00:00:00Z"));

        verify(getUseCase).getById(id);
        verifyNoMoreInteractions(getUseCase);
        verifyNoInteractions(createUseCase);
    }

    @Test
    @DisplayName("GET /transactions/{id}: internal server error")
    void get_shouldReturn500WhenUseCaseThrows() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        when(getUseCase.getById(eq(id))).thenThrow(new IllegalArgumentException("Transaction not found: " + id));

        // When
        //  Then

        mockMvc.perform(get("/transactions/{transactionExternalId}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Error interno del servidor"));

        verify(getUseCase).getById(id);
        verifyNoMoreInteractions(getUseCase);
        verifyNoInteractions(createUseCase);
    }
}
