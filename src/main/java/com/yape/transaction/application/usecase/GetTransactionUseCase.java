package com.yape.transaction.application.usecase;

import com.yape.transaction.application.dto.TransactionView;

import java.util.UUID;

public interface GetTransactionUseCase {
    TransactionView getById(UUID transactionExternalId);
}