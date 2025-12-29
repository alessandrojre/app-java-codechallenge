package com.yape.transaction.infrastructure.inbound.rest.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
