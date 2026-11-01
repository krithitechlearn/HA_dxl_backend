package com.ha.dxl.automation.dto;

import com.ha.dxl.automation.domain.TriggerType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for automation rule data.
 * @see API-027 to API-034, US-009, US-010
 */
public record AutomationRuleResponse(
        UUID id,
        String name,
        String description,
        UUID ownerUserId,
        TriggerType triggerType,
        Map<String, Object> triggerConfig,
        boolean isEnabled,
        int version,
        OffsetDateTime lastTriggeredAt,
        List<AutomationActionResponse> actions,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
