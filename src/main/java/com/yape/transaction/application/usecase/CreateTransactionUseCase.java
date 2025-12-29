package com.yape.transaction.application.usecase;

import com.yape.transaction.application.dto.CreateTransactionCommand;
import java.util.UUID;

public interface CreateTransactionUseCase {
    UUID create(CreateTransactionCommand command);
}