package com.yape.transaction.infrastructure.config;

import com.yape.transaction.application.service.TransactionService;
import com.yape.transaction.domain.port.TransactionEventPublisherPort;
import com.yape.transaction.domain.port.TransactionRepositoryPort;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeansConfig {

    @Bean
    public TransactionService transactionService(TransactionRepositoryPort transactionRepository,
                                                 TransactionEventPublisherPort transactionEventPublisher) {
        return new TransactionService(transactionRepository, transactionEventPublisher);
    }

    @Bean(name = "kafkaTopicsProperties")
    @ConfigurationProperties(prefix = "yape.kafka.topics")
    public KafkaTopicsProperties kafkaTopicsProperties() {
        return new KafkaTopicsProperties();
    }

}