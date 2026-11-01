package com.ha.dxl.user.dto;

import jakarta.validation.constraints.*;

/**
 * Request DTO for updating an existing user.
 * @see API-007, US-002
 */
public record UserUpdateRequest(
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only alphanumeric characters and underscores")
        String username,

        @Email(message = "Email must be a valid email address")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        String role
) {}
