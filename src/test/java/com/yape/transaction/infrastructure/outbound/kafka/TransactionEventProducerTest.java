package com.yape.transaction.infrastructure.outbound.kafka;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.domain.model.TransactionStatus;
import com.yape.transaction.domain.model.TransactionType;
import com.yape.transaction.infrastructure.config.KafkaTopicsProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionEventProducerTest {

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    @Captor
    ArgumentCaptor<Object> payloadCaptor;

    @Test
    void returnKafkaEventWhenTransactionIsCreated() {

        KafkaTopicsProperties topics = new KafkaTopicsProperties();
        topics.setTransactionCreated("transaction.create");
        topics.setTransactionStatusUpdated("transaction.status.updated");

        TransactionEventProducer producer = new TransactionEventProducer(kafkaTemplate, topics);

        UUID txId = UUID.randomUUID();
        UUID debit = UUID.randomUUID();
        UUID credit = UUID.randomUUID();

        Transaction tx = new Transaction(
                txId,
                debit,
                credit,
                TransactionType.TRANSFER,
                new BigDecimal("50.00"),
                TransactionStatus.PENDING,
                Instant.parse("2026-01-05T10:00:00Z")
        );

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        Mono<Void> result = producer.publishTransactionCreated(tx);

        StepVerifier.create(result)
                .verifyComplete();

        verify(kafkaTemplate, times(1))
                .send(eq("transaction.create"), eq(txId.toString()), payloadCaptor.capture());

        Object payload = payloadCaptor.getValue();
        assertThat(payload).isInstanceOf(TransactionCreatedEvent.class);

        TransactionCreatedEvent event = (TransactionCreatedEvent) payload;
        assertThat(event.getTransactionExternalId()).isEqualTo(txId);
        assertThat(event.getAccountExternalIdDebit()).isEqualTo(debit);
        assertThat(event.getAccountExternalIdCredit()).isEqualTo(credit);
        assertThat(event.getTransferTypeId()).isEqualTo(1);
        assertThat(event.getValue()).isEqualByComparingTo("50.00");
        assertThat(event.getCreatedAt()).isEqualTo(Instant.parse("2026-01-05T10:00:00Z"));
    }
}
