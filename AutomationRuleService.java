package com.ha.dxl.automation.service;

import com.ha.dxl.automation.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for automation rule management.
 * @see US-009, US-010
 */
public interface AutomationRuleService {

    /** @see API-027 */
    AutomationRuleResponse createRule(AutomationRuleCreateRequest request);

    /** @see API-029 */
    AutomationRuleResponse getRuleById(UUID id);

    /** @see API-028 */
    Page<AutomationRuleResponse> getAllRules(UUID ownerId, Pageable pageable);

    /** @see API-030 */
    AutomationRuleResponse updateRule(UUID id, AutomationRuleUpdateRequest request);

    /** @see API-031 */
    void deleteRule(UUID id);

    /** @see API-032 */
    AutomationRuleResponse toggleRule(UUID id, boolean enabled);

    /** @see API-033 */
    AutomationExecutionLogResponse triggerManually(UUID id);
}
