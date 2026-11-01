package com.ha.dxl.common.dto;

/**
 * Generic API response wrapper for single-item responses.
 */
public record ApiResponse<T>(
        String message,
        T data
) {}
