package com.ha.dxl.automation.repository;

import com.ha.dxl.automation.domain.AutomationRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for AutomationRule entities.
 * @see US-009, US-010
 */
@Repository
public interface AutomationRuleRepository extends JpaRepository<AutomationRule, UUID> {

    Page<AutomationRule> findAllByOwnerUserId(UUID ownerUserId, Pageable pageable);

    List<AutomationRule> findAllByIsEnabledTrue();

    @Query("SELECT r FROM AutomationRule r LEFT JOIN FETCH r.actions WHERE r.id = :id")
    Optional<AutomationRule> findByIdWithActions(@Param("id") UUID id);
}
