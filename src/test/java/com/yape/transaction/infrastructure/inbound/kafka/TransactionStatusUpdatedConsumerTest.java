package com.yape.transaction.infrastructure.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.transaction.application.usecase.UpdateTransactionStatusUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

class TransactionStatusUpdatedConsumerTest {

    private UpdateTransactionStatusUseCase useCase;
    private ObjectMapper objectMapper;
    private TransactionStatusUpdatedConsumer consumer;

    @BeforeEach
    void setUp() {
        // Given
        useCase = mock(UpdateTransactionStatusUseCase.class);
        objectMapper = mock(ObjectMapper.class);
        consumer = new TransactionStatusUpdatedConsumer(useCase, objectMapper);
    }

    @Test
    @DisplayName("onMessage: process valid message")
    void onMessage_shouldCallUseCaseWhenMessageValid() throws Exception {
        // Given
        UUID txId = UUID.randomUUID();
        String status = "APPROVED";
        String message = "{\"transactionExternalId\":\"" + txId + "\",\"status\":\"" + status + "\"}";

        TransactionStatusUpdatedEvent event = new TransactionStatusUpdatedEvent(txId, status);
        when(objectMapper.readValue(message, TransactionStatusUpdatedEvent.class)).thenReturn(event);

        // When
        consumer.onMessage(message);

        // Then
        verify(useCase).updateStatus(txId, status);
        verifyNoMoreInteractions(useCase);
        verify(objectMapper).readValue(message, TransactionStatusUpdatedEvent.class);
        verifyNoMoreInteractions(objectMapper);
    }

    @Test
    @DisplayName("onMessage: ignore invalid message")
    void onMessage_shouldNotThrowAndNotCallUseCaseWhenInvalidJson() throws Exception {
        // Given
        String message = "invalid-json";
        when(objectMapper.readValue(message, TransactionStatusUpdatedEvent.class))
                .thenThrow(new RuntimeException("boom"));

        // When
        consumer.onMessage(message);

        // Then
        verifyNoInteractions(useCase);
        verify(objectMapper).readValue(message, TransactionStatusUpdatedEvent.class);
        verifyNoMoreInteractions(objectMapper);
    }
}
