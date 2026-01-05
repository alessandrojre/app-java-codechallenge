package com.yape.transaction.domain.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TransactionType {
    TRANSFER(1);

    private final int id;

    TransactionType(int id) {
        this.id = id;
    }

    public static TransactionType fromId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction type ID cannot be null");
        }
        return Arrays.stream(values())
                .filter(type -> type.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported transaction type ID: " + id));
    }
}