package com.ha.dxl.telemetry.controller;

import com.ha.dxl.telemetry.dto.TelemetryIngestRequest;
import com.ha.dxl.telemetry.dto.TelemetryResponse;
import com.ha.dxl.telemetry.service.TelemetryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * REST controller for telemetry ingestion and querying.
 * @see US-007, US-008, API-025, API-026
 */
@RestController
@Tag(name = "Telemetry", description = "Telemetry ingestion and query endpoints")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    @PostMapping("/api/v1/telemetry")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM')")
    @Operation(summary = "Ingest telemetry", description = "API-025: Ingest telemetry data from a device agent")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Telemetry ingested"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<TelemetryResponse> ingestTelemetry(
            @Valid @RequestBody TelemetryIngestRequest request) {
        TelemetryResponse response = telemetryService.ingestTelemetry(request);
        return ResponseEntity.created(URI.create("/api/v1/telemetry/" + response.id())).body(response);
    }

    @GetMapping("/api/v1/devices/{deviceId}/telemetry")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Get device telemetry", description = "API-026: Get telemetry data for a device with date range filter")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Telemetry retrieved"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<Page<TelemetryResponse>> getDeviceTelemetry(
            @PathVariable UUID deviceId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @RequestParam(required = false) String metricName,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(telemetryService.getDeviceTelemetry(deviceId, from, to, metricName, pageable));
    }
}
