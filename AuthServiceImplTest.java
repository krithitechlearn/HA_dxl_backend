package com.ha.dxl.auth.service.impl;

import com.ha.dxl.exception.UnauthorizedException;
import com.ha.dxl.security.JwtTokenProvider;
import com.ha.dxl.user.domain.User;
import com.ha.dxl.user.domain.UserRole;
import com.ha.dxl.user.repository.UserRepository;
import com.ha.dxl.auth.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthServiceImpl.
 * @see US-001
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("admin");
        testUser.setPasswordHash("$2a$12$hashed");
        testUser.setRole(UserRole.ADMIN);
        testUser.setActive(true);
    }

    @Test
    @DisplayName("US-001: login_withValidCredentials_returnsLoginResponse")
    void login_withValidCredentials_returnsLoginResponse() {
        LoginRequest request = new LoginRequest("admin", "Admin@1234");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Admin@1234", testUser.getPasswordHash())).thenReturn(true);

        var response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.username()).isEqualTo("admin");
        assertThat(response.role()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("US-001: login_withInvalidPassword_throwsUnauthorizedException")
    void login_withInvalidPassword_throwsUnauthorizedException() {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    @DisplayName("US-001: login_withInactiveUser_throwsUnauthorizedException")
    void login_withInactiveUser_throwsUnauthorizedException() {
        testUser.setActive(false);
        LoginRequest request = new LoginRequest("admin", "Admin@1234");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("deactivated");
    }

    @Test
    @DisplayName("US-001: login_withUnknownUser_throwsUnauthorizedException")
    void login_withUnknownUser_throwsUnauthorizedException() {
        LoginRequest request = new LoginRequest("unknown", "password");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class);
    }
}
