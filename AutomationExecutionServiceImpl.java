package com.ha.dxl.automation.service.impl;

import com.ha.dxl.automation.domain.AutomationAction;
import com.ha.dxl.automation.domain.AutomationExecutionLog;
import com.ha.dxl.automation.domain.AutomationExecutionStatus;
import com.ha.dxl.automation.domain.AutomationRule;
import com.ha.dxl.automation.dto.AutomationExecutionLogResponse;
import com.ha.dxl.automation.mapper.AutomationRuleMapper;
import com.ha.dxl.automation.repository.AutomationExecutionLogRepository;
import com.ha.dxl.automation.service.AutomationExecutionService;
import com.ha.dxl.device.dto.DeviceCommandRequest;
import com.ha.dxl.device.service.DeviceCommandService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of AutomationExecutionService.
 * Executes automation rule actions in order and logs the result.
 * @see US-010, API-033, API-034
 */
@Service
@RequiredArgsConstructor
public class AutomationExecutionServiceImpl implements AutomationExecutionService {

    private static final Logger log = LoggerFactory.getLogger(AutomationExecutionServiceImpl.class);

    private final AutomationExecutionLogRepository logRepository;
    private final DeviceCommandService deviceCommandService;
    private final AutomationRuleMapper ruleMapper;

    @Override
    @Transactional
    public AutomationExecutionLog executeRule(AutomationRule rule) {
        List<AutomationAction> actions = rule.getActions();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (AutomationAction action : actions) {
            try {
                DeviceCommandRequest commandRequest = new DeviceCommandRequest(
                        action.getActionType().name(),
                        action.getActionPayload()
                );
                deviceCommandService.issueCommand(action.getTargetDevice().getId(), commandRequest);
                successCount++;
            } catch (Exception e) {
                log.warn("Action failed for rule {}: {}", rule.getId(), e.getMessage());
                errors.add("Action " + action.getId() + ": " + e.getMessage());
            }
        }

        AutomationExecutionStatus status;
        if (errors.isEmpty()) {
            status = AutomationExecutionStatus.SUCCESS;
        } else if (successCount > 0) {
            status = AutomationExecutionStatus.PARTIAL_FAILURE;
        } else {
            status = AutomationExecutionStatus.FAILURE;
        }

        AutomationExecutionLog executionLog = new AutomationExecutionLog();
        executionLog.setAutomationRule(rule);
        executionLog.setStatus(status);
        executionLog.setTriggerDetail("MANUAL");
        executionLog.setErrorMessage(errors.isEmpty() ? null : String.join("; ", errors));
        executionLog.setExecutedAt(OffsetDateTime.now());

        AutomationExecutionLog saved = logRepository.save(executionLog);
        log.info("Automation rule executed: ruleId={}, status={}", rule.getId(), status);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutomationExecutionLogResponse> getExecutionLogs(java.util.UUID ruleId, Pageable pageable) {
        return logRepository.findAllByAutomationRuleId(ruleId, pageable)
                .map(ruleMapper::toExecutionLogResponse);
    }
}
