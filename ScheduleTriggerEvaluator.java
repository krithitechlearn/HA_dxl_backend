package com.ha.dxl.automation.strategy;

import com.ha.dxl.automation.domain.AutomationRule;
import com.ha.dxl.automation.domain.TriggerType;
import org.springframework.stereotype.Component;

/**
 * Evaluates SCHEDULE trigger type.
 * For MVP, always returns true when called (actual cron evaluation is deferred).
 * @see US-009, US-010
 */
@Component
public class ScheduleTriggerEvaluator implements TriggerEvaluator {

    @Override
    public boolean supports(TriggerType triggerType) {
        return TriggerType.SCHEDULE == triggerType;
    }

    @Override
    public boolean evaluate(AutomationRule rule) {
        // MVP: schedule evaluation deferred; returns true for manual triggers
        return true;
    }
}
