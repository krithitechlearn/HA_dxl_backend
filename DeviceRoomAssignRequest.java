package com.ha.dxl.device.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request DTO for assigning a device to a room.
 * @see API-020, US-003, US-004
 */
public record DeviceRoomAssignRequest(
        @NotNull(message = "Room ID must not be null")
        UUID roomId
) {}
