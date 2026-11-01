package com.ha.dxl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end integration test for the main application flow.
 * Uses Testcontainers with a real PostgreSQL 15 instance.
 * @see US-001 (main happy path)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class HaDxlApplicationIntegrationTest {

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
        registry.add("app.jwt.secret", () -> "ThisIsAVeryLongSecretKeyForHaDxlBackendApplicationThatMustBeAtLeast256BitsLong");
        registry.add("app.jwt.access-token-expiry-ms", () -> "900000");
        registry.add("app.jwt.refresh-token-expiry-ms", () -> "604800000");
        registry.add("app.cors.allowed-origins", () -> "http://localhost:3000");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("US-001: actuatorHealth_returns200")
    void actuatorHealth_returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("US-001: loginWithSeedAdmin_returns200")
    void loginWithSeedAdmin_returns200() {
        String loginBody = "{\"username\":\"admin\",\"password\":\"Admin@1234\"}";
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(loginBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/auth/login", entity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("US-001: protectedEndpoint_withoutAuth_returns401")
    void protectedEndpoint_withoutAuth_returns401() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/users", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
