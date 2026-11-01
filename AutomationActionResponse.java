package com.ha.dxl.automation.dto;

import com.ha.dxl.automation.domain.ActionType;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for a single automation action.
 * @see US-009, US-010
 */
public record AutomationActionResponse(
        UUID id,
        UUID targetDeviceId,
        ActionType actionType,
        Map<String, Object> actionPayload,
        int executionOrder,
        OffsetDateTime createdAt
) {}
