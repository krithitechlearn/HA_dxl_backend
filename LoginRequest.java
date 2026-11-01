package com.ha.dxl.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for user login.
 * @see API-001, US-001
 */
public record LoginRequest(
        @NotBlank(message = "Username must not be blank")
        String username,

        @NotBlank(message = "Password must not be blank")
        String password
) {}
