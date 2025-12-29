package com.yape.transaction.domain.port;

import com.yape.transaction.domain.model.Transaction;

public interface TransactionEventPublisherPort {
    void publishTransactionCreated(Transaction transaction);
}