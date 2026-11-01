package com.ha.dxl.device.repository;

import com.ha.dxl.device.domain.DeviceCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository for DeviceCommand entities.
 * @see US-006
 */
@Repository
public interface DeviceCommandRepository extends JpaRepository<DeviceCommand, UUID> {

    Page<DeviceCommand> findAllByDeviceId(UUID deviceId, Pageable pageable);

    Page<DeviceCommand> findAllByIssuedByUserId(UUID issuedByUserId, Pageable pageable);
}
