package com.ha.dxl.automation.strategy;

import com.ha.dxl.automation.domain.AutomationRule;
import com.ha.dxl.automation.domain.TriggerType;

/**
 * Strategy interface for evaluating automation rule trigger conditions.
 * Implementations allow adding new trigger types without modifying existing code (OCP).
 * @see US-009, US-010, LLD section 6.3
 */
public interface TriggerEvaluator {

    /**
     * Returns true if this evaluator supports the given trigger type.
     */
    boolean supports(TriggerType triggerType);

    /**
     * Evaluates whether the trigger condition is met for the given rule.
     */
    boolean evaluate(AutomationRule rule);
}
