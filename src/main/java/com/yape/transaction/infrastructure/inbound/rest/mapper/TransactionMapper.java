package com.yape.transaction.infrastructure.inbound.rest.mapper;

import com.yape.transaction.domain.model.Transaction;
import com.yape.transaction.infrastructure.inbound.rest.dto.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                new TransactionResponse.TransactionTypeResponse(transaction.getType().name()),
                new TransactionResponse.TransactionStatusResponse(transaction.getStatus().name()),
                transaction.getValue(),
                transaction.getCreatedAt()
        );
    }
}