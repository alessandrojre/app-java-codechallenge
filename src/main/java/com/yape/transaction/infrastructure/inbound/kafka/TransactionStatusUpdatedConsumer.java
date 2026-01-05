package com.yape.transaction.infrastructure.inbound.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yape.transaction.application.usecase.UpdateTransactionStatusUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionStatusUpdatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionStatusUpdatedConsumer.class);

    private final UpdateTransactionStatusUseCase useCase;
    private final ObjectMapper objectMapper;

    public TransactionStatusUpdatedConsumer(UpdateTransactionStatusUseCase useCase,
                                            ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "#{@kafkaTopicsProperties.transactionStatusUpdated}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onMessage(String message) {
        Mono.just(message)
                .<TransactionStatusUpdatedEvent>handle((msg, sink) -> {
                    try {
                        sink.next(objectMapper.readValue(msg, TransactionStatusUpdatedEvent.class));
                    } catch (Exception exception) {
                        sink.error(new RuntimeException("Error deserializando: " + exception.getMessage()));
                    }
                })
                .flatMap(event -> useCase.updateStatus(event.getTransactionExternalId(), event.getStatus()))
                .doOnError(error -> log.error("Error crítico procesando evento de estado: {}", error.getMessage()))
                .subscribe();
    }
}
