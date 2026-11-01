package com.ha.dxl.device.service.impl;

import com.ha.dxl.device.domain.Device;
import com.ha.dxl.device.domain.DeviceStatus;
import com.ha.dxl.device.domain.DeviceType;
import com.ha.dxl.device.dto.DeviceCommandRequest;
import com.ha.dxl.device.mapper.DeviceCommandMapper;
import com.ha.dxl.device.repository.DeviceCommandRepository;
import com.ha.dxl.device.repository.DeviceRepository;
import com.ha.dxl.exception.BusinessRuleException;
import com.ha.dxl.exception.ResourceNotFoundException;
import com.ha.dxl.security.SecurityUtils;
import com.ha.dxl.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DeviceCommandServiceImpl.
 * @see US-006
 */
@ExtendWith(MockitoExtension.class)
class DeviceCommandServiceImplTest {

    @Mock
    private DeviceCommandRepository commandRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DeviceCommandMapper commandMapper;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private DeviceCommandServiceImpl deviceCommandService;

    private Device testDevice;
    private UUID deviceId;

    @BeforeEach
    void setUp() {
        deviceId = UUID.randomUUID();
        testDevice = new Device();
        testDevice.setId(deviceId);
        testDevice.setName("Test Light");
        testDevice.setDeviceType(DeviceType.LIGHT);
        testDevice.setStatus(DeviceStatus.ONLINE);
        testDevice.setActive(true);
    }

    @Test
    @DisplayName("US-006: issueCommand_whenDeviceInMaintenance_throwsBusinessRuleException")
    void issueCommand_whenDeviceInMaintenance_throwsBusinessRuleException() {
        testDevice.setStatus(DeviceStatus.MAINTENANCE);
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(testDevice));

        DeviceCommandRequest request = new DeviceCommandRequest("TURN_ON", null);

        assertThatThrownBy(() -> deviceCommandService.issueCommand(deviceId, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("MAINTENANCE");
    }

    @Test
    @DisplayName("US-006: issueCommand_whenCommandIncompatible_throwsBusinessRuleException")
    void issueCommand_whenCommandIncompatible_throwsBusinessRuleException() {
        testDevice.setDeviceType(DeviceType.LOCK);
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(testDevice));

        DeviceCommandRequest request = new DeviceCommandRequest("SET_TEMPERATURE", null);

        assertThatThrownBy(() -> deviceCommandService.issueCommand(deviceId, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("COMMAND_NOT_COMPATIBLE");
    }

    @Test
    @DisplayName("US-006: issueCommand_whenDeviceNotFound_throwsResourceNotFoundException")
    void issueCommand_whenDeviceNotFound_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(deviceRepository.findById(unknownId)).thenReturn(Optional.empty());

        DeviceCommandRequest request = new DeviceCommandRequest("TURN_ON", null);

        assertThatThrownBy(() -> deviceCommandService.issueCommand(unknownId, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("US-006: getCommandHistory_whenDeviceNotFound_throwsResourceNotFoundException")
    void getCommandHistory_whenDeviceNotFound_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(deviceRepository.existsById(unknownId)).thenReturn(false);

        assertThatThrownBy(() -> deviceCommandService.getCommandHistory(unknownId,
                org.springframework.data.domain.PageRequest.of(0, 20)))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
