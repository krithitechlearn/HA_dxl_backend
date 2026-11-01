package com.ha.dxl.room.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response DTO for room data.
 * @see API-010 to API-014, US-003
 */
public record RoomResponse(
        UUID id,
        String name,
        String description,
        String floorLevel,
        UUID createdByUserId,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
