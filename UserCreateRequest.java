package com.ha.dxl.user.dto;

import jakarta.validation.constraints.*;

/**
 * Request DTO for creating a new user.
 * @see API-004, US-002
 */
public record UserCreateRequest(
        @NotBlank(message = "Username must not be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only alphanumeric characters and underscores")
        String username,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be a valid email address")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotBlank(message = "Password must not be blank")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':,./<>?]).{8,}$",
                message = "Password must be at least 8 characters with 1 uppercase, 1 digit, and 1 special character"
        )
        String password,

        @NotNull(message = "Role must not be null")
        String role
) {}
