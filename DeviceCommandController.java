package com.ha.dxl.device.controller;

import com.ha.dxl.device.dto.DeviceCommandRequest;
import com.ha.dxl.device.dto.DeviceCommandResponse;
import com.ha.dxl.device.service.DeviceCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for device command operations.
 * @see US-006, API-023, API-024
 */
@RestController
@RequestMapping("/api/v1/devices/{deviceId}/commands")
@Tag(name = "Device Commands", description = "Device command issuance and history")
@RequiredArgsConstructor
public class DeviceCommandController {

    private final DeviceCommandService deviceCommandService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('GUEST')")
    @Operation(summary = "Issue command", description = "API-023: Issue a command to a device")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Command issued"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "422", description = "Device not commandable or incompatible command")
    })
    public ResponseEntity<DeviceCommandResponse> issueCommand(
            @PathVariable UUID deviceId,
            @Valid @RequestBody DeviceCommandRequest request) {
        DeviceCommandResponse response = deviceCommandService.issueCommand(deviceId, request);
        return ResponseEntity.created(URI.create("/api/v1/devices/" + deviceId + "/commands/" + response.id()))
                .body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Get command history", description = "API-024: Get command history for a device")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Command history retrieved"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<Page<DeviceCommandResponse>> getCommandHistory(
            @PathVariable UUID deviceId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(deviceCommandService.getCommandHistory(deviceId, pageable));
    }
}
