package com.ha.dxl.automation.service.impl;

import com.ha.dxl.audit.service.AuditLogService;
import com.ha.dxl.automation.domain.AutomationRule;
import com.ha.dxl.automation.domain.TriggerType;
import com.ha.dxl.automation.dto.AutomationRuleCreateRequest;
import com.ha.dxl.automation.mapper.AutomationRuleMapper;
import com.ha.dxl.automation.repository.AutomationRuleRepository;
import com.ha.dxl.automation.service.AutomationExecutionService;
import com.ha.dxl.automation.strategy.TriggerEvaluator;
import com.ha.dxl.device.repository.DeviceRepository;
import com.ha.dxl.exception.BusinessRuleException;
import com.ha.dxl.exception.ResourceNotFoundException;
import com.ha.dxl.security.SecurityUtils;
import com.ha.dxl.user.domain.User;
import com.ha.dxl.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AutomationRuleServiceImpl.
 * @see US-009, US-010
 */
@ExtendWith(MockitoExtension.class)
class AutomationRuleServiceImplTest {

    @Mock
    private AutomationRuleRepository ruleRepository;
    @Mock
    private AutomationExecutionService executionService;
    @Mock
    private List<TriggerEvaluator> triggerEvaluators;
    @Mock
    private AutomationRuleMapper ruleMapper;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private AutomationRuleServiceImpl automationRuleService;

    private UUID ruleId;
    private AutomationRule testRule;
    private User testUser;

    @BeforeEach
    void setUp() {
        ruleId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("testuser");

        testRule = new AutomationRule();
        testRule.setId(ruleId);
        testRule.setName("Test Rule");
        testRule.setTriggerType(TriggerType.MANUAL);
        testRule.setEnabled(true);
        testRule.setOwnerUser(testUser);
    }

    @Test
    @DisplayName("US-009: createRule_withEmptyActions_throwsBusinessRuleException")
    void createRule_withEmptyActions_throwsBusinessRuleException() {
        AutomationRuleCreateRequest request = new AutomationRuleCreateRequest(
                "Test Rule", null, "MANUAL", null, List.of());

        assertThatThrownBy(() -> automationRuleService.createRule(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("At least one action is required");
    }

    @Test
    @DisplayName("US-010: triggerManually_whenRuleDisabled_throwsBusinessRuleException")
    void triggerManually_whenRuleDisabled_throwsBusinessRuleException() {
        testRule.setEnabled(false);
        when(ruleRepository.findByIdWithActions(ruleId)).thenReturn(Optional.of(testRule));

        assertThatThrownBy(() -> automationRuleService.triggerManually(ruleId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("disabled");
    }

    @Test
    @DisplayName("US-010: getRuleById_whenNotFound_throwsResourceNotFoundException")
    void getRuleById_whenNotFound_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(ruleRepository.findByIdWithActions(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> automationRuleService.getRuleById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
