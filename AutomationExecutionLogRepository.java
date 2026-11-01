package com.ha.dxl.automation.repository;

import com.ha.dxl.automation.domain.AutomationExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository for AutomationExecutionLog entities.
 * @see US-010
 */
@Repository
public interface AutomationExecutionLogRepository extends JpaRepository<AutomationExecutionLog, UUID> {

    Page<AutomationExecutionLog> findAllByAutomationRuleId(UUID automationRuleId, Pageable pageable);
}
