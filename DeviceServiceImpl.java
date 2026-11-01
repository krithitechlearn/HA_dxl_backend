package com.ha.dxl.device.service.impl;

import com.ha.dxl.audit.service.AuditLogService;
import com.ha.dxl.device.domain.*;
import com.ha.dxl.device.dto.*;
import com.ha.dxl.device.mapper.DeviceMapper;
import com.ha.dxl.device.repository.DeviceRepository;
import com.ha.dxl.device.repository.UserDeviceAccessRepository;
import com.ha.dxl.device.service.DeviceService;
import com.ha.dxl.exception.ConflictException;
import com.ha.dxl.exception.ResourceNotFoundException;
import com.ha.dxl.room.domain.Room;
import com.ha.dxl.room.repository.RoomRepository;
import com.ha.dxl.security.SecurityUtils;
import com.ha.dxl.user.domain.User;
import com.ha.dxl.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Implementation of DeviceService.
 * @see US-003, US-004, US-005, API-015 to API-022
 */
@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceServiceImpl.class);

    private final DeviceRepository deviceRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserDeviceAccessRepository userDeviceAccessRepository;
    private final DeviceMapper deviceMapper;
    private final AuditLogService auditLogService;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public DeviceResponse registerDevice(DeviceCreateRequest request) {
        // Validate device type
        DeviceType deviceType;
        try {
            deviceType = DeviceType.valueOf(request.deviceType());
        } catch (IllegalArgumentException e) {
            throw new com.ha.dxl.exception.BusinessRuleException("VALIDATION_ERROR",
                    "Invalid device type: " + request.deviceType());
        }

        Device device = deviceMapper.toEntity(request);
        device.setDeviceType(deviceType);

        if (request.roomId() != null) {
            Room room = roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room", request.roomId().toString()));
            device.setRoom(room);
        }
        if (request.assignedUserId() != null) {
            User user = userRepository.findById(request.assignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.assignedUserId().toString()));
            device.setAssignedUser(user);
        }

        Device saved = deviceRepository.save(device);
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        log.info("Device registered: name={}, type={}", saved.getName(), saved.getDeviceType());
        auditLogService.logAction(currentUser, "REGISTER_DEVICE", "DEVICE", saved.getId().toString(),
                null, saved.getName(), null);
        return deviceMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DeviceResponse getDeviceById(UUID id) {
        Device device = deviceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", id.toString()));
        return deviceMapper.toResponse(device);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceResponse> getAllDevices(UUID roomId, String status, String type, Pageable pageable) {
        if (roomId != null) {
            return deviceRepository.findAllByRoomId(roomId, pageable).map(deviceMapper::toResponse);
        }
        if (status != null) {
            DeviceStatus deviceStatus = DeviceStatus.valueOf(status);
            return deviceRepository.findAllByStatus(deviceStatus, pageable).map(deviceMapper::toResponse);
        }
        if (type != null) {
            DeviceType deviceType = DeviceType.valueOf(type);
            return deviceRepository.findAllByDeviceType(deviceType, pageable).map(deviceMapper::toResponse);
        }
        return deviceRepository.findAll(pageable).map(deviceMapper::toResponse);
    }

    @Override
    @Transactional
    public DeviceResponse updateDevice(UUID id, DeviceUpdateRequest request) {
        Device device = deviceRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", id.toString()));

        deviceMapper.updateEntity(request, device);

        if (request.roomId() != null) {
            Room room = roomRepository.findById(request.roomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room", request.roomId().toString()));
            device.setRoom(room);
        } else {
            device.setRoom(null);
        }
        if (request.assignedUserId() != null) {
            User user = userRepository.findById(request.assignedUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", request.assignedUserId().toString()));
            device.setAssignedUser(user);
        } else {
            device.setAssignedUser(null);
        }

        Device saved = deviceRepository.save(device);
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        auditLogService.logAction(currentUser, "UPDATE_DEVICE", "DEVICE", saved.getId().toString(),
                null, saved.getName(), null);
        return deviceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deactivateDevice(UUID id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", id.toString()));
        device.setActive(false);
        deviceRepository.save(device);
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        log.info("Device deactivated: id={}", id);
        auditLogService.logAction(currentUser, "DEACTIVATE_DEVICE", "DEVICE", id.toString(), null, null, null);
    }

    @Override
    @Transactional
    public DeviceResponse assignDeviceToRoom(UUID deviceId, UUID roomId) {
        Device device = deviceRepository.findByIdWithDetails(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device", deviceId.toString()));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));
        device.setRoom(room);
        Device saved = deviceRepository.save(device);
        return deviceMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void grantDeviceAccess(UUID deviceId, UUID userId, AccessLevel accessLevel) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device", deviceId.toString()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        if (userDeviceAccessRepository.existsByUserIdAndDeviceId(userId, deviceId)) {
            throw new ConflictException("Device access already granted for user: " + userId);
        }

        UserDeviceAccess access = new UserDeviceAccess();
        access.setUser(user);
        access.setDevice(device);
        access.setAccessLevel(accessLevel);
        access.setGrantedAt(OffsetDateTime.now());
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        User grantedBy = userRepository.findByUsername(currentUser).orElse(null);
        access.setGrantedByUser(grantedBy);
        userDeviceAccessRepository.save(access);
        auditLogService.logAction(currentUser, "GRANT_DEVICE_ACCESS", "DEVICE", deviceId.toString(),
                null, accessLevel.name(), null);
    }

    @Override
    @Transactional
    public void revokeDeviceAccess(UUID deviceId, UUID userId) {
        if (!userDeviceAccessRepository.existsByUserIdAndDeviceId(userId, deviceId)) {
            throw new ResourceNotFoundException("UserDeviceAccess", userId + "/" + deviceId);
        }
        userDeviceAccessRepository.deleteByUserIdAndDeviceId(userId, deviceId);
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        auditLogService.logAction(currentUser, "REVOKE_DEVICE_ACCESS", "DEVICE", deviceId.toString(),
                null, null, null);
    }
}
