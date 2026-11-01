package com.ha.dxl.room.controller;

import com.ha.dxl.room.dto.RoomCreateRequest;
import com.ha.dxl.room.dto.RoomResponse;
import com.ha.dxl.room.dto.RoomUpdateRequest;
import com.ha.dxl.room.service.RoomService;
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
 * REST controller for room management.
 * @see US-003, API-010 to API-014
 */
@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Rooms", description = "Room management endpoints")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create room", description = "API-010: Create a new room")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Room created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Room name already exists")
    })
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomCreateRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.created(URI.create("/api/v1/rooms/" + response.id())).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('GUEST')")
    @Operation(summary = "List all rooms", description = "API-011: Get paginated list of rooms")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rooms retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Page<RoomResponse>> getAllRooms(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(roomService.getAllRooms(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('GUEST')")
    @Operation(summary = "Get room by ID", description = "API-012: Get room details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room found"),
            @ApiResponse(responseCode = "404", description = "Room not found")
    })
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable UUID id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update room", description = "API-013: Update room details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Room updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "409", description = "Room name conflict")
    })
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable UUID id,
                                                    @Valid @RequestBody RoomUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete room", description = "API-014: Delete a room")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Room deleted"),
            @ApiResponse(responseCode = "404", description = "Room not found"),
            @ApiResponse(responseCode = "422", description = "Room has active devices")
    })
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
