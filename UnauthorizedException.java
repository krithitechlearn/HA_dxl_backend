package com.ha.dxl.exception;

/**
 * Exception thrown when authentication fails or token is invalid.
 * Maps to HTTP 401.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
