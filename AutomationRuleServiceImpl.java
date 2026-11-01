package com.ha.dxl.automation.service.impl;

import com.ha.dxl.audit.service.AuditLogService;
import com.ha.dxl.automation.domain.AutomationAction;
import com.ha.dxl.automation.domain.AutomationExecutionLog;
import com.ha.dxl.automation.domain.AutomationRule;
import com.ha.dxl.automation.domain.TriggerType;
import com.ha.dxl.automation.dto.*;
import com.ha.dxl.automation.mapper.AutomationRuleMapper;
import com.ha.dxl.automation.repository.AutomationRuleRepository;
import com.ha.dxl.automation.service.AutomationExecutionService;
import com.ha.dxl.automation.service.AutomationRuleService;
import com.ha.dxl.automation.strategy.TriggerEvaluator;
import com.ha.dxl.device.domain.Device;
import com.ha.dxl.device.repository.DeviceRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of AutomationRuleService.
 * Uses Strategy Pattern for trigger evaluation.
 * @see US-009, US-010, API-027 to API-034
 */
@Service
@RequiredArgsConstructor
public class AutomationRuleServiceImpl implements AutomationRuleService {

    private static final Logger log = LoggerFactory.getLogger(AutomationRuleServiceImpl.class);

    private final AutomationRuleRepository ruleRepository;
    private final AutomationExecutionService executionService;
    private final List<TriggerEvaluator> triggerEvaluators;
    private final AutomationRuleMapper ruleMapper;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public AutomationRuleResponse createRule(AutomationRuleCreateRequest request) {
        if (request.actions() == null || request.actions().isEmpty()) {
            throw new BusinessRuleException("BUSINESS_RULE_VIOLATION", "At least one action is required");
        }

        TriggerType triggerType;
        try {
            triggerType = TriggerType.valueOf(request.triggerType());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("VALIDATION_ERROR", "Invalid trigger type: " + request.triggerType());
        }

        String currentUsername = securityUtils.getCurrentUsername().orElse("SYSTEM");
        User owner = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUsername));

        AutomationRule rule = new AutomationRule();
        rule.setName(request.name());
        rule.setDescription(request.description());
        rule.setOwnerUser(owner);
        rule.setTriggerType(triggerType);
        rule.setTriggerConfig(request.triggerConfig() != null ? request.triggerConfig() : new java.util.HashMap<>());
        rule.setEnabled(true);

        List<AutomationAction> actions = buildActions(request.actions(), rule);
        rule.setActions(actions);

        AutomationRule saved = ruleRepository.save(rule);
        log.info("Automation rule created: name={}, triggerType={}", saved.getName(), saved.getTriggerType());
        auditLogService.logAction(currentUsername, "CREATE_AUTOMATION_RULE", "AUTOMATION_RULE",
                saved.getId().toString(), null, saved.getName(), null);
        return ruleMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AutomationRuleResponse getRuleById(UUID id) {
        AutomationRule rule = ruleRepository.findByIdWithActions(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutomationRule", id.toString()));
        return ruleMapper.toResponse(rule);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AutomationRuleResponse> getAllRules(UUID ownerId, Pageable pageable) {
        if (ownerId != null) {
            return ruleRepository.findAllByOwnerUserId(ownerId, pageable).map(ruleMapper::toResponse);
        }
        return ruleRepository.findAll(pageable).map(ruleMapper::toResponse);
    }

    @Override
    @Transactional
    public AutomationRuleResponse updateRule(UUID id, AutomationRuleUpdateRequest request) {
        AutomationRule rule = ruleRepository.findByIdWithActions(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutomationRule", id.toString()));

        if (request.actions() == null || request.actions().isEmpty()) {
            throw new BusinessRuleException("BUSINESS_RULE_VIOLATION", "At least one action is required");
        }

        TriggerType triggerType;
        try {
            triggerType = TriggerType.valueOf(request.triggerType());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("VALIDATION_ERROR", "Invalid trigger type: " + request.triggerType());
        }

        rule.setName(request.name());
        rule.setDescription(request.description());
        rule.setTriggerType(triggerType);
        rule.setTriggerConfig(request.triggerConfig() != null ? request.triggerConfig() : new java.util.HashMap<>());

        rule.getActions().clear();
        List<AutomationAction> newActions = buildActions(request.actions(), rule);
        rule.getActions().addAll(newActions);

        AutomationRule saved = ruleRepository.save(rule);
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        auditLogService.logAction(currentUser, "UPDATE_AUTOMATION_RULE", "AUTOMATION_RULE",
                saved.getId().toString(), null, saved.getName(), null);
        return ruleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteRule(UUID id) {
        AutomationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutomationRule", id.toString()));
        ruleRepository.delete(rule);
        String currentUser = securityUtils.getCurrentUsername().orElse("SYSTEM");
        log.info("Automation rule deleted: id={}", id);
        auditLogService.logAction(currentUser, "DELETE_AUTOMATION_RULE", "AUTOMATION_RULE",
                id.toString(), rule.getName(), null, null);
    }

    @Override
    @Transactional
    public AutomationRuleResponse toggleRule(UUID id, boolean enabled) {
        AutomationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutomationRule", id.toString()));
        rule.setEnabled(enabled);
        AutomationRule saved = ruleRepository.save(rule);
        return ruleMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AutomationExecutionLogResponse triggerManually(UUID id) {
        AutomationRule rule = ruleRepository.findByIdWithActions(id)
                .orElseThrow(() -> new ResourceNotFoundException("AutomationRule", id.toString()));

        if (!rule.isEnabled()) {
            throw new BusinessRuleException("BUSINESS_RULE_VIOLATION",
                    "Cannot trigger a disabled automation rule");
        }

        AutomationExecutionLog executionLog = executionService.executeRule(rule);
        rule.setLastTriggeredAt(java.time.OffsetDateTime.now());
        ruleRepository.save(rule);
        return ruleMapper.toExecutionLogResponse(executionLog);
    }

    private List<AutomationAction> buildActions(List<AutomationActionRequest> actionRequests,
                                                 AutomationRule rule) {
        List<AutomationAction> actions = new ArrayList<>();
        for (AutomationActionRequest req : actionRequests) {
            Device device = deviceRepository.findById(req.targetDeviceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Device", req.targetDeviceId().toString()));

            com.ha.dxl.automation.domain.ActionType actionType;
            try {
                actionType = com.ha.dxl.automation.domain.ActionType.valueOf(req.actionType());
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("VALIDATION_ERROR", "Invalid action type: " + req.actionType());
            }

            AutomationAction action = new AutomationAction();
            action.setAutomationRule(rule);
            action.setTargetDevice(device);
            action.setActionType(actionType);
            action.setActionPayload(req.actionPayload() != null ? req.actionPayload() : new java.util.HashMap<>());
            action.setExecutionOrder(req.executionOrder());
            actions.add(action);
        }
        return actions;
    }
}
