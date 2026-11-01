package com.ha.dxl.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request DTO for granting device access to a user.
 * @see API-021, US-005
 */
public record DeviceAccessGrantRequest(
        @NotNull(message = "User ID must not be null")
        UUID userId,

        @NotBlank(message = "Access level must not be blank")
        String accessLevel
) {}
