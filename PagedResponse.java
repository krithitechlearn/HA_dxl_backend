package com.ha.dxl.common.dto;

import java.util.List;

/**
 * Generic paginated response wrapper.
 * @see A-05
 */
public record PagedResponse<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int number,
        int size
) {}
