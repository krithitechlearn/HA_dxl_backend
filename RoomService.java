package com.ha.dxl.room.service;

import com.ha.dxl.room.dto.RoomCreateRequest;
import com.ha.dxl.room.dto.RoomResponse;
import com.ha.dxl.room.dto.RoomUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for room management operations.
 * @see US-003
 */
public interface RoomService {

    /** @see API-010 */
    RoomResponse createRoom(RoomCreateRequest request);

    /** @see API-012 */
    RoomResponse getRoomById(UUID id);

    /** @see API-011 */
    Page<RoomResponse> getAllRooms(Pageable pageable);

    /** @see API-013 */
    RoomResponse updateRoom(UUID id, RoomUpdateRequest request);

    /** @see API-014 */
    void deleteRoom(UUID id);
}
