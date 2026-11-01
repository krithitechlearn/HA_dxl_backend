package com.ha.dxl.audit.controller;

import com.ha.dxl.audit.dto.AuditLogResponse;
import com.ha.dxl.audit.service.impl.AuditLogServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * REST controller for audit log querying.
 * @see US-011, API-035, API-036
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@Tag(name = "Audit Logs", description = "Audit log query endpoints (ADMIN only)")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogServiceImpl auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Query audit logs", description = "API-035: Query audit logs with filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit logs retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(entityType, entityId, actor, from, to, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit log entry", description = "API-036: Get a specific audit log entry by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit log found"),
            @ApiResponse(responseCode = "404", description = "Audit log not found")
    })
    public ResponseEntity<AuditLogResponse> getAuditLogById(@PathVariable UUID id) {
        return ResponseEntity.ok(auditLogService.getAuditLogById(id));
    }
}
