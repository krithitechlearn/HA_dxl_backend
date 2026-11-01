package com.ha.dxl.audit.mapper;

import com.ha.dxl.audit.domain.AuditLog;
import com.ha.dxl.audit.dto.AuditLogResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for AuditLog entity and DTOs.
 * @see US-011
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AuditLogMapper {

    AuditLogResponse toResponse(AuditLog auditLog);
}
