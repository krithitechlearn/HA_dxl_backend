package com.ha.dxl.user.repository;

import com.ha.dxl.user.domain.User;
import com.ha.dxl.user.domain.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for User entities.
 * @see US-001, US-002
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findAllByRole(UserRole role, Pageable pageable);

    long countByRoleAndIsActiveTrue(UserRole role);
}
