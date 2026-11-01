package com.ha.dxl.device.service;

import com.ha.dxl.device.domain.AccessLevel;
import com.ha.dxl.device.dto.DeviceCreateRequest;
import com.ha.dxl.device.dto.DeviceResponse;
import com.ha.dxl.device.dto.DeviceUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for device management operations.
 * @see US-003, US-004, US-005
 */
public interface DeviceService {

    /** @see API-015 */
    DeviceResponse registerDevice(DeviceCreateRequest request);

    /** @see API-017 */
    DeviceResponse getDeviceById(UUID id);

    /** @see API-016 */
    Page<DeviceResponse> getAllDevices(UUID roomId, String status, String type, Pageable pageable);

    /** @see API-018 */
    DeviceResponse updateDevice(UUID id, DeviceUpdateRequest request);

    /** @see API-019 */
    void deactivateDevice(UUID id);

    /** @see API-020 */
    DeviceResponse assignDeviceToRoom(UUID deviceId, UUID roomId);

    /** @see API-021 */
    void grantDeviceAccess(UUID deviceId, UUID userId, AccessLevel accessLevel);

    /** @see API-022 */
    void revokeDeviceAccess(UUID deviceId, UUID userId);
}
