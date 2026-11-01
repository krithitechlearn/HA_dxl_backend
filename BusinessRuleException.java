package com.ha.dxl.exception;

import lombok.Getter;

/**
 * Exception thrown when a business rule is violated.
 * Maps to HTTP 422.
 */
@Getter
public class BusinessRuleException extends RuntimeException {

    private final String errorCode;

    public BusinessRuleException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
