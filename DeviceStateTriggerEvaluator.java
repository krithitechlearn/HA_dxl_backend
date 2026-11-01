package com.ha.dxl.automation.strategy;

import com.ha.dxl.automation.domain.AutomationRule;
import com.ha.dxl.automation.domain.TriggerType;
import com.ha.dxl.device.domain.Device;
import com.ha.dxl.device.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Evaluates DEVICE_STATE trigger type.
 * Checks if a target device's status matches the configured expected status.
 * @see US-009, US-010
 */
@Component
@RequiredArgsConstructor
public class DeviceStateTriggerEvaluator implements TriggerEvaluator {

    private final DeviceRepository deviceRepository;

    @Override
    public boolean supports(TriggerType triggerType) {
        return TriggerType.DEVICE_STATE == triggerType;
    }

    @Override
    public boolean evaluate(AutomationRule rule) {
        Map<String, Object> config = rule.getTriggerConfig();
        if (config == null) return false;
        String deviceIdStr = (String) config.get("deviceId");
        String expectedStatus = (String) config.get("expectedStatus");
        if (deviceIdStr == null || expectedStatus == null) return false;

        Optional<Device> device = deviceRepository.findById(UUID.fromString(deviceIdStr));
        return device.map(d -> d.getStatus().name().equals(expectedStatus)).orElse(false);
    }
}
