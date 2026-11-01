package com.ha.dxl.user.service.impl;

import com.ha.dxl.audit.service.AuditLogService;
import com.ha.dxl.exception.BusinessRuleException;
import com.ha.dxl.exception.ConflictException;
import com.ha.dxl.exception.ResourceNotFoundException;
import com.ha.dxl.user.domain.User;
import com.ha.dxl.user.domain.UserRole;
import com.ha.dxl.user.dto.ChangePasswordRequest;
import com.ha.dxl.user.dto.UserCreateRequest;
import com.ha.dxl.user.dto.UserResponse;
import com.ha.dxl.user.mapper.UserMapper;
import com.ha.dxl.user.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl.
 * @see US-002
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("$2a$12$hashedpassword");
        testUser.setRole(UserRole.USER);
        testUser.setActive(true);
    }

    @Test
    @DisplayName("US-002: createUser_whenUsernameExists_throwsConflictException")
    void createUser_whenUsernameExists_throwsConflictException() {
        UserCreateRequest request = new UserCreateRequest(
                "testuser", "new@example.com", "Password@1", "USER");
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    @DisplayName("US-002: createUser_whenEmailExists_throwsConflictException")
    void createUser_whenEmailExists_throwsConflictException() {
        UserCreateRequest request = new UserCreateRequest(
                "newuser", "test@example.com", "Password@1", "USER");
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    @DisplayName("US-002: createUser_withValidData_returnsUserResponse")
    void createUser_withValidData_returnsUserResponse() {
        UserCreateRequest request = new UserCreateRequest(
                "newuser", "new@example.com", "Password@1", "USER");
        UserResponse expectedResponse = new UserResponse(userId, "newuser", "new@example.com",
                UserRole.USER, true, null, null);

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(testUser);
        when(passwordEncoder.encode(any())).thenReturn("$2a$12$encoded");
        when(userRepository.save(any())).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(expectedResponse);
        doNothing().when(auditLogService).logAction(any(), any(), any(), any(), any(), any(), any());

        UserResponse result = userService.createUser(request);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("newuser");
    }

    @Test
    @DisplayName("US-002: getUserById_whenNotFound_throwsResourceNotFoundException")
    void getUserById_whenNotFound_throwsResourceNotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(userRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(unknownId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("US-002: deactivateUser_whenLastAdmin_throwsBusinessRuleException")
    void deactivateUser_whenLastAdmin_throwsBusinessRuleException() {
        testUser.setRole(UserRole.ADMIN);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.countByRoleAndIsActiveTrue(UserRole.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> userService.deactivateUser(userId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("last active ADMIN");
    }

    @Test
    @DisplayName("US-002: changePassword_whenCurrentPasswordWrong_throwsBusinessRuleException")
    void changePassword_whenCurrentPasswordWrong_throwsBusinessRuleException() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongpassword", "NewPass@1");
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(userId, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Current password is incorrect");
    }
}
