package com.ha.dxl.automation.dto;

import com.ha.dxl.automation.domain.AutomationExecutionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for automation execution log entries.
 * @see API-033, API-034, US-010
 */
public record AutomationExecutionLogResponse(
        UUID id,
        UUID automationRuleId,
        AutomationExecutionStatus status,
        String triggerDetail,
        String errorMessage,
        OffsetDateTime executedAt
) {}
