package com.ha.dxl.telemetry.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for ingesting telemetry data.
 * @see API-025, US-007
 */
public record TelemetryIngestRequest(
        @NotNull(message = "Device ID must not be null")
        UUID deviceId,

        @NotBlank(message = "Metric name must not be blank")
        @Size(max = 100, message = "Metric name must not exceed 100 characters")
        String metricName,

        @NotNull(message = "Metric value must not be null")
        BigDecimal metricValue,

        @Size(max = 30, message = "Unit must not exceed 30 characters")
        String unit,

        Map<String, Object> rawPayload,

        @NotNull(message = "Recorded at must not be null")
        @PastOrPresent(message = "Recorded at must not be in the future")
        OffsetDateTime recordedAt
) {}
