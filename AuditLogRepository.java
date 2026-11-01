package com.ha.dxl.audit.repository;

import com.ha.dxl.audit.domain.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Spring Data JPA repository for AuditLog entities.
 * @see US-011
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:entityType IS NULL OR a.entityType = :entityType) " +
           "AND (:entityId IS NULL OR a.entityId = :entityId) " +
           "AND (:actor IS NULL OR a.actorUsername = :actor) " +
           "AND (:from IS NULL OR a.occurredAt >= :from) " +
           "AND (:to IS NULL OR a.occurredAt <= :to)")
    Page<AuditLog> findByFilters(
            @Param("entityType") String entityType,
            @Param("entityId") String entityId,
            @Param("actor") String actor,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            Pageable pageable);
}
