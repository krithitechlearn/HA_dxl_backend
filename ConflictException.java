package com.ha.dxl.exception;

/**
 * Exception thrown when a unique constraint conflict occurs.
 * Maps to HTTP 409.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
