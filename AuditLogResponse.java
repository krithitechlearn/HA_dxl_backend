package com.ha.dxl.audit.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for audit log entries.
 * @see API-035, API-036, US-011
 */
public record AuditLogResponse(
        UUID id,
        String actorUsername,
        String action,
        String entityType,
        String entityId,
        Object beforeState,
        Object afterState,
        String ipAddress,
        OffsetDateTime occurredAt
) {}
