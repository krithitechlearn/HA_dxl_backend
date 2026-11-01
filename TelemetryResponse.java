package com.ha.dxl.telemetry.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for telemetry data.
 * @see API-025, API-026, US-007, US-008
 */
public record TelemetryResponse(
        UUID id,
        UUID deviceId,
        String metricName,
        BigDecimal metricValue,
        String unit,
        Map<String, Object> rawPayload,
        OffsetDateTime recordedAt
) {}
