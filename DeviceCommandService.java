package com.ha.dxl.device.service;

import com.ha.dxl.device.dto.DeviceCommandRequest;
import com.ha.dxl.device.dto.DeviceCommandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for device command operations.
 * @see US-006
 */
public interface DeviceCommandService {

    /** @see API-023 */
    DeviceCommandResponse issueCommand(UUID deviceId, DeviceCommandRequest request);

    /** @see API-024 */
    Page<DeviceCommandResponse> getCommandHistory(UUID deviceId, Pageable pageable);
}
