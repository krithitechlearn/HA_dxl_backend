package com.ha.dxl.device.dto;

import jakarta.validation.constraints.*;

import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for registering a new device.
 * @see API-015, US-003, US-004
 */
public record DeviceCreateRequest(
        @NotBlank(message = "Device name must not be blank")
        @Size(min = 1, max = 100, message = "Device name must be between 1 and 100 characters")
        String name,

        @NotBlank(message = "Device type must not be blank")
        String deviceType,

        @Size(max = 100, message = "Manufacturer must not exceed 100 characters")
        String manufacturer,

        @Size(max = 100, message = "Model number must not exceed 100 characters")
        String modelNumber,

        @Size(max = 50, message = "Firmware version must not exceed 50 characters")
        String firmwareVersion,

        Map<String, Object> attributes,

        UUID roomId,

        UUID assignedUserId
) {}
