package com.ha.dxl.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Standard error response schema for all API errors.
 * @see LLD section 6.6
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String errorCode,
        String message,
        String path,
        String traceId,
        List<FieldError> fieldErrors
) {
    public record FieldError(
            String field,
            Object rejectedValue,
            String message
    ) {}
}
