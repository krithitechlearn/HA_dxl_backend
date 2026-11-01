package com.ha.dxl.device.repository;

import com.ha.dxl.device.domain.Device;
import com.ha.dxl.device.domain.DeviceStatus;
import com.ha.dxl.device.domain.DeviceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for Device entities.
 * @see US-003, US-004, US-005, US-006, US-012
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID>, JpaSpecificationExecutor<Device> {

    Page<Device> findAllByRoomId(UUID roomId, Pageable pageable);

    Page<Device> findAllByAssignedUserId(UUID assignedUserId, Pageable pageable);

    Page<Device> findAllByStatus(DeviceStatus status, Pageable pageable);

    Page<Device> findAllByDeviceType(DeviceType deviceType, Pageable pageable);

    Page<Device> findAllByIsActiveTrue(Pageable pageable);

    boolean existsByRoomIdAndIsActiveTrue(UUID roomId);

    @Modifying
    @Query("UPDATE Device d SET d.lastSeenAt = :lastSeenAt, d.status = 'ONLINE' WHERE d.id = :deviceId")
    void updateLastSeenAt(@Param("deviceId") UUID deviceId, @Param("lastSeenAt") OffsetDateTime lastSeenAt);

    @Query("SELECT d FROM Device d LEFT JOIN FETCH d.room LEFT JOIN FETCH d.assignedUser WHERE d.id = :id")
    Optional<Device> findByIdWithDetails(@Param("id") UUID id);
}
