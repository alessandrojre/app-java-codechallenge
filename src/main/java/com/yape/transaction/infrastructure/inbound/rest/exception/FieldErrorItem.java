package com.yape.transaction.infrastructure.inbound.rest.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorItem {

    private String field;
    private String message;
}
