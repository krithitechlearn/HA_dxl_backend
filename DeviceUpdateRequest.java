package com.ha.dxl.device.dto;

import jakarta.validation.constraints.*;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for updating a device. Includes version for optimistic locking.
 * @see API-018, US-004, A-11
 */
public record DeviceUpdateRequest(
        @NotBlank(message = "Device name must not be blank")
        @Size(min = 1, max = 100, message = "Device name must be between 1 and 100 characters")
        String name,

        @Size(max = 100, message = "Manufacturer must not exceed 100 characters")
        String manufacturer,

        @Size(max = 100, message = "Model number must not exceed 100 characters")
        String modelNumber,

        @Size(max = 50, message = "Firmware version must not exceed 50 characters")
        String firmwareVersion,

        Map<String, Object> attributes,

        UUID roomId,

        UUID assignedUserId,

        @NotNull(message = "Version must not be null for optimistic locking")
        Integer version
) {}
