package com.ha.dxl.device.controller;

import com.ha.dxl.device.domain.AccessLevel;
import com.ha.dxl.device.dto.*;
import com.ha.dxl.device.service.DeviceService;
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
 * REST controller for device management and access control.
 * @see US-003, US-004, US-005, API-015 to API-022
 */
@RestController
@RequestMapping("/api/v1/devices")
@Tag(name = "Devices", description = "Device management endpoints")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register device", description = "API-015: Register a new smart home device")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Device registered"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Room or user not found")
    })
    public ResponseEntity<DeviceResponse> registerDevice(@Valid @RequestBody DeviceCreateRequest request) {
        DeviceResponse response = deviceService.registerDevice(request);
        return ResponseEntity.created(URI.create("/api/v1/devices/" + response.id())).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('GUEST')")
    @Operation(summary = "List devices", description = "API-016: Get paginated list of devices with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Devices retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Page<DeviceResponse>> getAllDevices(
            @RequestParam(required = false) UUID roomId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(deviceService.getAllDevices(roomId, status, type, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('GUEST')")
    @Operation(summary = "Get device by ID", description = "API-017: Get device details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceResponse> getDeviceById(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.getDeviceById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Update device", description = "API-018: Update device details (includes version for optimistic locking)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "409", description = "Optimistic lock conflict")
    })
    public ResponseEntity<DeviceResponse> updateDevice(@PathVariable UUID id,
                                                        @Valid @RequestBody DeviceUpdateRequest request) {
        return ResponseEntity.ok(deviceService.updateDevice(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate device", description = "API-019: Deactivate a device")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device deactivated"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<Void> deactivateDevice(@PathVariable UUID id) {
        deviceService.deactivateDevice(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/room")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign device to room", description = "API-020: Assign a device to a room")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device assigned to room"),
            @ApiResponse(responseCode = "404", description = "Device or room not found")
    })
    public ResponseEntity<DeviceResponse> assignDeviceToRoom(@PathVariable UUID id,
                                                              @Valid @RequestBody DeviceRoomAssignRequest request) {
        return ResponseEntity.ok(deviceService.assignDeviceToRoom(id, request.roomId()));
    }

    @PostMapping("/{id}/access")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Grant device access", description = "API-021: Grant a user access to a device")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Access granted"),
            @ApiResponse(responseCode = "404", description = "Device or user not found"),
            @ApiResponse(responseCode = "409", description = "Access already granted")
    })
    public ResponseEntity<Void> grantDeviceAccess(@PathVariable UUID id,
                                                   @Valid @RequestBody DeviceAccessGrantRequest request) {
        AccessLevel accessLevel;
        try {
            accessLevel = AccessLevel.valueOf(request.accessLevel());
        } catch (IllegalArgumentException e) {
            throw new com.ha.dxl.exception.BusinessRuleException("VALIDATION_ERROR",
                    "Invalid access level: " + request.accessLevel());
        }
        deviceService.grantDeviceAccess(id, request.userId(), accessLevel);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{id}/access/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revoke device access", description = "API-022: Revoke a user's access to a device")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Access revoked"),
            @ApiResponse(responseCode = "404", description = "Access record not found")
    })
    public ResponseEntity<Void> revokeDeviceAccess(@PathVariable UUID id,
                                                    @PathVariable UUID userId) {
        deviceService.revokeDeviceAccess(id, userId);
        return ResponseEntity.noContent().build();
    }
}
