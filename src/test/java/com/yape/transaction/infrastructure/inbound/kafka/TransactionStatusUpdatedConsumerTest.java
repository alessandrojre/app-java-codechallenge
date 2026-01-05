package com.yape.transaction.infrastructure.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.transaction.application.usecase.UpdateTransactionStatusUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionStatusUpdatedConsumerTest {

    @Mock
    UpdateTransactionStatusUseCase useCase;

    private ObjectMapper objectMapper;
    private TransactionStatusUpdatedConsumer consumer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        consumer = new TransactionStatusUpdatedConsumer(useCase, objectMapper);
    }

    @Test
    void returnUpdateStatusWhenMessageIsValid() throws Exception {
        UUID txId = UUID.randomUUID();
        String message = objectMapper.writeValueAsString(new TransactionStatusUpdatedEvent(txId, "APPROVED"));

        when(useCase.updateStatus(txId, "APPROVED")).thenReturn(Mono.empty());

        consumer.onMessage(message);

        verify(useCase, timeout(500).times(1)).updateStatus(txId, "APPROVED");
        verifyNoMoreInteractions(useCase);
    }

    @Test
    void returnNothingWhenMessageIsInvalid() {
        String invalidJson = "{not-valid-json";

        consumer.onMessage(invalidJson);

        verify(useCase, after(300).never()).updateStatus(any(), any());
        verifyNoInteractions(useCase);
    }

    @Test
    void returnUpdateStatusWhenStatusIsLowercase() throws Exception {
        UUID transactionID = UUID.randomUUID();
        String message = objectMapper.writeValueAsString(new TransactionStatusUpdatedEvent(transactionID, "approved"));

        when(useCase.updateStatus(transactionID, "approved")).thenReturn(Mono.empty());

        consumer.onMessage(message);

        verify(useCase, timeout(500).times(1)).updateStatus(transactionID, "approved");
    }
}
