package com.ha.dxl.automation.repository;

import com.ha.dxl.automation.domain.AutomationAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for AutomationAction entities.
 * @see US-009, US-010
 */
@Repository
public interface AutomationActionRepository extends JpaRepository<AutomationAction, UUID> {

    List<AutomationAction> findAllByAutomationRuleIdOrderByExecutionOrderAsc(UUID automationRuleId);
}
