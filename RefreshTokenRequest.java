package com.ha.dxl.auth.dto;

/**
 * Request DTO for token refresh (used when cookie-based refresh is not available).
 * @see API-002, US-001
 */
public record RefreshTokenRequest(
        String refreshToken
) {}
