package com.ha.dxl.room.service.impl;

import com.ha.dxl.audit.service.AuditLogService;
import com.ha.dxl.device.repository.DeviceRepository;
import com.ha.dxl.exception.BusinessRuleException;
import com.ha.dxl.exception.ConflictException;
import com.ha.dxl.exception.ResourceNotFoundException;
import com.ha.dxl.room.domain.Room;
import com.ha.dxl.room.dto.RoomCreateRequest;
import com.ha.dxl.room.dto.RoomResponse;
import com.ha.dxl.room.dto.RoomUpdateRequest;
import com.ha.dxl.room.mapper.RoomMapper;
import com.ha.dxl.room.repository.RoomRepository;
import com.ha.dxl.room.service.RoomService;
import com.ha.dxl.user.repository.UserRepository;
import com.ha.dxl.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of RoomService.
 * @see US-003, API-010 to API-014
 */
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomServiceImpl.class);

    private final RoomRepository roomRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final RoomMapper roomMapper;
    private final AuditLogService auditLogService;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public RoomResponse createRoom(RoomCreateRequest request) {
        if (roomRepository.existsByName(request.name())) {
            throw new ConflictException("Room name already exists: " + request.name());
        }
        Room room = roomMapper.toEntity(request);
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        room.setCreatedByUser(userRepository.findByUsername(currentUser).orElse(null));
        Room saved = roomRepository.save(room);
        log.info("Room created: name={}", saved.getName());
        auditLogService.logAction(currentUser, "CREATE_ROOM", "ROOM", saved.getId().toString(), null,
                saved.getName(), null);
        return roomMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getRoomById(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id.toString()));
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomResponse> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable).map(roomMapper::toResponse);
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(UUID id, RoomUpdateRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id.toString()));

        if (!room.getName().equals(request.name()) && roomRepository.existsByName(request.name())) {
            throw new ConflictException("Room name already exists: " + request.name());
        }
        String before = room.getName();
        roomMapper.updateEntity(request, room);
        Room saved = roomRepository.save(room);
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        auditLogService.logAction(currentUser, "UPDATE_ROOM", "ROOM", saved.getId().toString(),
                before, saved.getName(), null);
        return roomMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRoom(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id.toString()));

        if (deviceRepository.existsByRoomIdAndIsActiveTrue(id)) {
            throw new BusinessRuleException("BUSINESS_RULE_VIOLATION",
                    "Cannot delete room with active devices");
        }
        roomRepository.delete(room);
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        log.info("Room deleted: id={}", id);
        auditLogService.logAction(currentUser, "DELETE_ROOM", "ROOM", id.toString(), room.getName(), null, null);
    }
}
