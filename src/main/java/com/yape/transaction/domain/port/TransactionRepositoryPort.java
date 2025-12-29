package com.yape.transaction.domain.port;

import com.yape.transaction.domain.model.Transaction;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepositoryPort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID transactionId);
}