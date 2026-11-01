package com.ha.dxl.user.dto;

import com.ha.dxl.user.domain.UserRole;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for user data. Password hash is never included.
 * @see API-004 to API-009, US-002
 */
public record UserResponse(
        UUID id,
        String username,
        String email,
        UserRole role,
        boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
