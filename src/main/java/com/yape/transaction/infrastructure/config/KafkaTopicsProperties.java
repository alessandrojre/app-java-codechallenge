package com.yape.transaction.infrastructure.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public class KafkaTopicsProperties {

    @NotBlank
    private String transactionCreated;

    @NotBlank
    private String transactionStatusUpdated;

    public String getTransactionCreated() {
        return transactionCreated;
    }

    public void setTransactionCreated(String transactionCreated) {
        this.transactionCreated = transactionCreated;
    }

    public String getTransactionStatusUpdated() {
        return transactionStatusUpdated;
    }

    public void setTransactionStatusUpdated(String transactionStatusUpdated) {
        this.transactionStatusUpdated = transactionStatusUpdated;
    }
}
