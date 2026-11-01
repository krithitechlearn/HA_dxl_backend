package com.ha.dxl.room.dto;

import jakarta.validation.constraints.*;

/**
 * Request DTO for updating a room.
 * @see API-013, US-003
 */
public record RoomUpdateRequest(
        @NotBlank(message = "Room name must not be blank")
        @Size(min = 1, max = 100, message = "Room name must be between 1 and 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @Size(max = 50, message = "Floor level must not exceed 50 characters")
        String floorLevel
) {}
