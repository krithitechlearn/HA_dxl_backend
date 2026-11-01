package com.ha.dxl.device.dto;

import com.ha.dxl.device.domain.DeviceStatus;
import com.ha.dxl.device.domain.DeviceType;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for device data.
 * @see API-015 to API-022, US-003, US-004, US-005
 */
public record DeviceResponse(
        UUID id,
        String name,
        DeviceType deviceType,
        DeviceStatus status,
        String manufacturer,
        String modelNumber,
        String firmwareVersion,
        Map<String, Object> attributes,
        UUID roomId,
        String roomName,
        UUID assignedUserId,
        int version,
        boolean isActive,
        OffsetDateTime lastSeenAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
