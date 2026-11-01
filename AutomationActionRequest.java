package com.ha.dxl.automation.dto;

import jakarta.validation.constraints.*;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for a single automation action.
 * @see US-009, US-010
 */
public record AutomationActionRequest(
        @NotNull(message = "Target device ID must not be null")
        UUID targetDeviceId,

        @NotBlank(message = "Action type must not be blank")
        String actionType,

        Map<String, Object> actionPayload,

        @Min(value = 1, message = "Execution order must be at least 1")
        int executionOrder
) {}
