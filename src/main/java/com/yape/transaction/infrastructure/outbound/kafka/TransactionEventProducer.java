package com.yape.transaction.infrastructure.outbound.kafka;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.port.TransactionEventPublisherPort;
import com.yape.transaction.infrastructure.config.KafkaTopicsProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionEventProducer implements TransactionEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties topics;

    public TransactionEventProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                    KafkaTopicsProperties topics) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
    }

    @Override
    public Mono<Void> publishTransactionCreated(Transaction transaction) {

        TransactionCreatedEvent transactionCreatedEvent = new TransactionCreatedEvent(
                transaction.getId(),
                transaction.getAccountExternalIdDebit(),
                transaction.getAccountExternalIdCredit(),
                transaction.getType().getId(),
                transaction.getValue(),
                transaction.getCreatedAt()
        );

        return Mono.fromCompletionStage(() ->
                kafkaTemplate.send(
                        topics.getTransactionCreated(),
                        transaction.getId().toString(),
                        transactionCreatedEvent
                )
        ).then();
    }

}
