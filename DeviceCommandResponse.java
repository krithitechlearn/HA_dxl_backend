package com.ha.dxl.device.dto;

import com.ha.dxl.device.domain.CommandStatus;
import com.ha.dxl.device.domain.CommandType;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for device command data.
 * @see API-023, API-024, US-006
 */
public record DeviceCommandResponse(
        UUID id,
        UUID deviceId,
        UUID issuedByUserId,
        CommandType commandType,
        Map<String, Object> commandPayload,
        CommandStatus status,
        String errorMessage,
        OffsetDateTime issuedAt,
        OffsetDateTime completedAt
) {}
