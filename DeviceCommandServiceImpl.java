package com.ha.dxl.device.service.impl;

import com.ha.dxl.device.domain.*;
import com.ha.dxl.device.dto.DeviceCommandRequest;
import com.ha.dxl.device.dto.DeviceCommandResponse;
import com.ha.dxl.device.mapper.DeviceCommandMapper;
import com.ha.dxl.device.repository.DeviceCommandRepository;
import com.ha.dxl.device.repository.DeviceRepository;
import com.ha.dxl.device.service.DeviceCommandService;
import com.ha.dxl.exception.BusinessRuleException;
import com.ha.dxl.exception.ResourceNotFoundException;
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
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of DeviceCommandService.
 * Validates command compatibility with device type before persisting.
 * @see US-006, API-023, API-024
 */
@Service
@RequiredArgsConstructor
public class DeviceCommandServiceImpl implements DeviceCommandService {

    private static final Logger log = LoggerFactory.getLogger(DeviceCommandServiceImpl.class);

    private final DeviceCommandRepository commandRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final DeviceCommandMapper commandMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public DeviceCommandResponse issueCommand(UUID deviceId, DeviceCommandRequest request) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device", deviceId.toString()));

        if (device.getStatus() == DeviceStatus.MAINTENANCE) {
            throw new BusinessRuleException("DEVICE_NOT_COMMANDABLE",
                    "Device is in MAINTENANCE status and cannot accept commands");
        }

        CommandType commandType;
        try {
            commandType = CommandType.valueOf(request.commandType());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("VALIDATION_ERROR", "Invalid command type: " + request.commandType());
        }

        validateCommandCompatibility(device.getDeviceType(), commandType);

        String currentUsername = securityUtils.getCurrentUsername().orElse("SYSTEM");
        User issuedBy = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUsername));

        DeviceCommand command = new DeviceCommand();
        command.setDevice(device);
        command.setIssuedByUser(issuedBy);
        command.setCommandType(commandType);
        command.setCommandPayload(request.commandPayload() != null ? request.commandPayload() : new java.util.HashMap<>());
        command.setStatus(CommandStatus.PENDING);
        command.setIssuedAt(OffsetDateTime.now());

        DeviceCommand saved = commandRepository.save(command);
        log.info("Command issued: deviceId={}, commandType={}", deviceId, commandType);
        return commandMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DeviceCommandResponse> getCommandHistory(UUID deviceId, Pageable pageable) {
        if (!deviceRepository.existsById(deviceId)) {
            throw new ResourceNotFoundException("Device", deviceId.toString());
        }
        return commandRepository.findAllByDeviceId(deviceId, pageable).map(commandMapper::toResponse);
    }

    /**
     * Validates that the given command type is compatible with the device type.
     * @see LLD section 6.5
     */
    private void validateCommandCompatibility(DeviceType deviceType, CommandType commandType) {
        Set<CommandType> allowed = switch (deviceType) {
            case LIGHT -> Set.of(CommandType.TURN_ON, CommandType.TURN_OFF,
                    CommandType.SET_BRIGHTNESS, CommandType.SET_COLOR);
            case THERMOSTAT -> Set.of(CommandType.TURN_ON, CommandType.TURN_OFF,
                    CommandType.SET_TEMPERATURE);
            case LOCK -> Set.of(CommandType.LOCK, CommandType.UNLOCK);
            case SENSOR -> Set.of(CommandType.TURN_ON, CommandType.TURN_OFF);
            case SWITCH -> Set.of(CommandType.TURN_ON, CommandType.TURN_OFF);
            case CAMERA -> Set.of(CommandType.TURN_ON, CommandType.TURN_OFF);
        };
        if (!allowed.contains(commandType)) {
            throw new BusinessRuleException("COMMAND_NOT_COMPATIBLE",
                    "Command " + commandType + " is not compatible with device type " + deviceType);
        }
    }
}
