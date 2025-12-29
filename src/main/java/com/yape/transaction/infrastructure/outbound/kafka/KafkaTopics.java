package com.yape.transaction.infrastructure.outbound.kafka;

public final class KafkaTopics {
    private KafkaTopics() {}

    public static final String TRANSACTION_CREATED = "transaction.created";
    public static final String TRANSACTION_STATUS_UPDATED = "transaction.status.updated";
}