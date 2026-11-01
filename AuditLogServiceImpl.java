package com.ha.dxl.audit.service.impl;

import com.ha.dxl.audit.domain.AuditLog;
import com.ha.dxl.audit.dto.AuditLogResponse;
import com.ha.dxl.audit.mapper.AuditLogMapper;
import com.ha.dxl.audit.repository.AuditLogRepository;
import com.ha.dxl.audit.service.AuditLogService;
import com.ha.dxl.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Implementation of AuditLogService. Persists audit entries asynchronously.
 * @see US-011
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Async
    @Transactional
    public void logAction(String actor, String action, String entityType, String entityId,
                          Object before, Object after, String ipAddress) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActorUsername(actor);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setBeforeState(before);
        auditLog.setAfterState(after);
        auditLog.setIpAddress(ipAddress);
        auditLog.setOccurredAt(OffsetDateTime.now());
        auditLogRepository.save(auditLog);
        log.info("Audit log recorded: actor={}, action={}, entityType={}, entityId={}",
                actor, action, entityType, entityId);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(String entityType, String entityId, String actor,
                                                OffsetDateTime from, OffsetDateTime to, Pageable pageable) {
        return auditLogRepository.findByFilters(entityType, entityId, actor, from, to, pageable)
                .map(auditLogMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(UUID id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuditLog", id.toString()));
        return auditLogMapper.toResponse(auditLog);
    }
}
