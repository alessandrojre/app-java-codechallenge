package com.yape.transaction.infrastructure.config;

import com.yape.transaction.application.service.TransactionService;
import com.yape.transaction.domain.port.TransactionEventPublisherPort;
import com.yape.transaction.domain.port.TransactionRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationBeansConfig {

    @Bean
    public TransactionService transactionService(TransactionRepositoryPort repository,
                                                 TransactionEventPublisherPort publisher) {
        return new TransactionService(repository, publisher);
    }
}
