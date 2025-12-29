package com.yape.transaction.infrastructure.outbound.kafka;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.port.TransactionEventPublisherPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventProducer implements TransactionEventPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransactionEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishTransactionCreated(Transaction transaction) {
        TransactionCreatedEvent event = new TransactionCreatedEvent(
                transaction.getId(),
                transaction.getAccountExternalIdDebit(),
                transaction.getAccountExternalIdCredit(),
                mapTypeId(transaction),
                transaction.getValue(),
                transaction.getCreatedAt()
        );

        kafkaTemplate.send(KafkaTopics.TRANSACTION_CREATED, transaction.getId().toString(), event);
    }

    private Integer mapTypeId(Transaction transaction) {
        //  TRANSFER = 1
        return 1;
    }
}
