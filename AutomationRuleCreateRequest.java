package com.ha.dxl.automation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Map;

/**
 * Request DTO for creating an automation rule.
 * @see API-027, US-009
 */
public record AutomationRuleCreateRequest(
        @NotBlank(message = "Rule name must not be blank")
        @Size(min = 1, max = 100, message = "Rule name must be between 1 and 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotBlank(message = "Trigger type must not be blank")
        String triggerType,

        Map<String, Object> triggerConfig,

        @NotNull(message = "Actions list must not be null")
        @Size(min = 1, message = "At least one action is required")
        @Valid
        List<AutomationActionRequest> actions
) {}
