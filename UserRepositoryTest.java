package com.ha.dxl.user.repository;

import com.ha.dxl.user.domain.User;
import com.ha.dxl.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository integration tests for UserRepository using Testcontainers.
 * @see US-001, US-002
 */
@DataJpaTest
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("ha_dxl_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("US-002: findByUsername_whenExists_returnsUser")
    void findByUsername_whenExists_returnsUser() {
        // The admin user is seeded by V11__seed_admin_user.sql
        Optional<User> result = userRepository.findByUsername("admin");
        assertThat(result).isPresent();
        assertThat(result.get().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("US-002: findByUsername_whenNotExists_returnsEmpty")
    void findByUsername_whenNotExists_returnsEmpty() {
        Optional<User> result = userRepository.findByUsername("nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("US-002: existsByUsername_whenExists_returnsTrue")
    void existsByUsername_whenExists_returnsTrue() {
        assertThat(userRepository.existsByUsername("admin")).isTrue();
    }

    @Test
    @DisplayName("US-002: existsByEmail_whenExists_returnsTrue")
    void existsByEmail_whenExists_returnsTrue() {
        assertThat(userRepository.existsByEmail("admin@ha-dxl.local")).isTrue();
    }

    @Test
    @DisplayName("US-002: countByRoleAndIsActiveTrue_returnsCorrectCount")
    void countByRoleAndIsActiveTrue_returnsCorrectCount() {
        long count = userRepository.countByRoleAndIsActiveTrue(UserRole.ADMIN);
        assertThat(count).isGreaterThanOrEqualTo(1);
    }
}
