package com.yape.transaction.infrastructure.inbound.rest.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private List<FieldErrorItem> errors;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(String message, List<FieldErrorItem> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }

    public static ErrorResponse of(String message, List<FieldErrorItem> errors) {
        return new ErrorResponse(message, (errors == null || errors.isEmpty()) ? null : errors);
    }


}
