package com.ha.dxl.device.repository;

import com.ha.dxl.device.domain.UserDeviceAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserDeviceAccess entities.
 * @see US-005
 */
@Repository
public interface UserDeviceAccessRepository extends JpaRepository<UserDeviceAccess, UUID> {

    boolean existsByUserIdAndDeviceId(UUID userId, UUID deviceId);

    Optional<UserDeviceAccess> findByUserIdAndDeviceId(UUID userId, UUID deviceId);

    void deleteByUserIdAndDeviceId(UUID userId, UUID deviceId);
}
