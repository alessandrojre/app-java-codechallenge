package com.yape.transaction.infrastructure.outbound.kafka;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransactionEventProducerTest {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private TransactionEventProducer producer;

    @BeforeEach
    void setUp() {
        // Given
        kafkaTemplate = mock(KafkaTemplate.class);
        producer = new TransactionEventProducer(kafkaTemplate);
    }

    @Test
    @DisplayName("Send TransactionCreatedEvent to correct topic with correct key and payload")
    void publishTransactionCreated_shouldSendEventToKafka() {
        // Given
        UUID id = UUID.randomUUID();
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();
        BigDecimal value = new BigDecimal("15.00");
        Instant createdAt = Instant.parse("2025-12-29T00:00:00Z");

        Transaction transaction = new Transaction(
                id,
                debit,
                credit,
                TransactionType.TRANSFER,
                value,
                TransactionStatus.PENDING,
                createdAt
        );

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);

        // When
        producer.publishTransactionCreated(transaction);

        // Then
        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                payloadCaptor.capture()
        );

        assertThat(topicCaptor.getValue()).isEqualTo(KafkaTopics.TRANSACTION_CREATED);
        assertThat(keyCaptor.getValue()).isEqualTo(id.toString());

        assertThat(payloadCaptor.getValue()).isInstanceOf(TransactionCreatedEvent.class);
        TransactionCreatedEvent event = (TransactionCreatedEvent) payloadCaptor.getValue();

        assertThat(event.transactionExternalId()).isEqualTo(id);
        assertThat(event.accountExternalIdDebit()).isEqualTo(debit);
        assertThat(event.accountExternalIdCredit()).isEqualTo(credit);
        assertThat(event.tranferTypeId()).isEqualTo(1); // TRANSFER = 1
        assertThat(event.value()).isEqualByComparingTo(value);
        assertThat(event.createdAt()).isEqualTo(createdAt);

        verifyNoMoreInteractions(kafkaTemplate);
    }
}
