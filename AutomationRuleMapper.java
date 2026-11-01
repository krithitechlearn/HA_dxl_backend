package com.ha.dxl.automation.mapper;

import com.ha.dxl.automation.domain.AutomationAction;
import com.ha.dxl.automation.domain.AutomationExecutionLog;
import com.ha.dxl.automation.domain.AutomationRule;
import com.ha.dxl.automation.dto.AutomationActionResponse;
import com.ha.dxl.automation.dto.AutomationExecutionLogResponse;
import com.ha.dxl.automation.dto.AutomationRuleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for AutomationRule entity and DTOs.
 * @see US-009, US-010
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AutomationRuleMapper {

    @Mapping(target = "ownerUserId", source = "ownerUser.id")
    AutomationRuleResponse toResponse(AutomationRule rule);

    @Mapping(target = "targetDeviceId", source = "targetDevice.id")
    AutomationActionResponse toActionResponse(AutomationAction action);

    @Mapping(target = "automationRuleId", source = "automationRule.id")
    AutomationExecutionLogResponse toExecutionLogResponse(AutomationExecutionLog log);
}
