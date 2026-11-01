package com.ha.dxl.telemetry.repository;

import com.ha.dxl.telemetry.domain.Telemetry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Spring Data JPA repository for Telemetry entities.
 * @see US-007, US-008
 */
@Repository
public interface TelemetryRepository extends JpaRepository<Telemetry, UUID> {

    @Query("SELECT t FROM Telemetry t WHERE t.device.id = :deviceId " +
           "AND (:from IS NULL OR t.recordedAt >= :from) " +
           "AND (:to IS NULL OR t.recordedAt <= :to) " +
           "AND (:metricName IS NULL OR t.metricName = :metricName)")
    Page<Telemetry> findByDeviceIdAndFilters(
            @Param("deviceId") UUID deviceId,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("metricName") String metricName,
            Pageable pageable);

    @Modifying
    @Query("DELETE FROM Telemetry t WHERE t.recordedAt < :cutoffDate")
    int deleteByRecordedAtBefore(@Param("cutoffDate") OffsetDateTime cutoffDate);
}
