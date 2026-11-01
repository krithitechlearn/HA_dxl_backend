package com.ha.dxl.user.dto;

import jakarta.validation.constraints.*;

/**
 * Request DTO for changing a user's password.
 * @see API-009, US-002
 */
public record ChangePasswordRequest(
        @NotBlank(message = "Current password must not be blank")
        String currentPassword,

        @NotBlank(message = "New password must not be blank")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':,./<>?]).{8,}$",
                message = "Password must be at least 8 characters with 1 uppercase, 1 digit, and 1 special character"
        )
        String newPassword
) {}
