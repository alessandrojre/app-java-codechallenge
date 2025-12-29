package com.yape.transaction.infrastructure.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.transaction.application.usecase.UpdateTransactionStatusUseCase;
import com.yape.transaction.infrastructure.outbound.kafka.KafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionStatusUpdatedConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(TransactionStatusUpdatedConsumer.class);
    private final UpdateTransactionStatusUseCase useCase;
    private final ObjectMapper objectMapper;

    public TransactionStatusUpdatedConsumer(UpdateTransactionStatusUseCase useCase,
                                            ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = KafkaTopics.TRANSACTION_STATUS_UPDATED,
            groupId = "transaction-service")
    public void onMessage(String message) {
        try {
            TransactionStatusUpdatedEvent event =
                    objectMapper.readValue(message, TransactionStatusUpdatedEvent.class);

            useCase.updateStatus(event.transactionExternalId(), event.status());

        } catch (Exception e) {
            log.error("Error processing transaction.status.updated message: {}", message, e);
        }
    }
}
