package com.ha.dxl.automation.service;

import com.ha.dxl.automation.domain.AutomationExecutionLog;
import com.ha.dxl.automation.domain.AutomationRule;
import com.ha.dxl.automation.dto.AutomationExecutionLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for automation rule execution.
 * @see US-010
 */
public interface AutomationExecutionService {

    /** Executes all actions of an automation rule and logs the result. */
    AutomationExecutionLog executeRule(AutomationRule rule);

    /** @see API-034 */
    Page<AutomationExecutionLogResponse> getExecutionLogs(UUID ruleId, Pageable pageable);
}
