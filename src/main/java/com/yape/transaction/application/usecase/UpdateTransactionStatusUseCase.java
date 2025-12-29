package com.yape.transaction.application.usecase;

import java.util.UUID;

public interface UpdateTransactionStatusUseCase {
    void updateStatus(UUID transactionExternalId, String newStatus);
}
