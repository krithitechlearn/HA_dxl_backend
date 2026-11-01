package com.ha.dxl.auth.dto;

import com.ha.dxl.user.domain.UserRole;

import java.util.UUID;

/**
 * Response DTO for successful login.
 * Tokens are set in HttpOnly cookies; this DTO contains user info only.
 * @see API-001, API-002, US-001
 */
public record LoginResponse(
        UUID userId,
        String username,
        UserRole role
) {}
