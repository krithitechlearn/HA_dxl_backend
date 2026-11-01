package com.ha.dxl.device.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

/**
 * Request DTO for issuing a command to a device.
 * @see API-023, US-006
 */
public record DeviceCommandRequest(
        @NotBlank(message = "Command type must not be blank")
        String commandType,

        Map<String, Object> commandPayload
) {}
