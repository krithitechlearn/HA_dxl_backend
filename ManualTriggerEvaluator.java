package com.ha.dxl.automation.strategy;

import com.ha.dxl.automation.domain.AutomationRule;
import com.ha.dxl.automation.domain.TriggerType;
import org.springframework.stereotype.Component;

/**
 * Evaluates MANUAL trigger type. Always returns true (manual triggers are always valid).
 * @see US-010
 */
@Component
public class ManualTriggerEvaluator implements TriggerEvaluator {

    @Override
    public boolean supports(TriggerType triggerType) {
        return TriggerType.MANUAL == triggerType || TriggerType.SUNRISE == triggerType
                || TriggerType.SUNSET == triggerType;
    }

    @Override
    public boolean evaluate(AutomationRule rule) {
        return true;
    }
}
