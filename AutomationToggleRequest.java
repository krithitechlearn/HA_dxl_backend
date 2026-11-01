package com.ha.dxl.automation.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for toggling an automation rule's enabled state.
 * @see API-032, US-010
 */
public record AutomationToggleRequest(
        @NotNull(message = "Enabled flag must not be null")
        Boolean enabled
) {}
