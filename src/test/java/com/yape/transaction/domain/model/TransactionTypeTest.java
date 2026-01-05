package com.yape.transaction.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TransactionTypeTest {

    @Test
    void returnTransferWhenIdIsValid() {
        Integer id = 1;

        TransactionType type = TransactionType.fromId(id);

        assertThat(type).isEqualTo(TransactionType.TRANSFER);
    }

    @Test
    void throwErrorWhenIdIsNull() {
        Integer id = null;

        assertThatThrownBy(() -> TransactionType.fromId(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    void throwErrorWhenIdIsNotSupported() {
        Integer id = 99;

        assertThatThrownBy(() -> TransactionType.fromId(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported transaction type ID: 99");
    }
}
