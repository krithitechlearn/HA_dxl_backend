package com.ha.dxl.user.service.impl;

import com.ha.dxl.audit.service.AuditLogService;
import com.ha.dxl.exception.BusinessRuleException;
import com.ha.dxl.exception.ConflictException;
import com.ha.dxl.exception.ResourceNotFoundException;
import com.ha.dxl.user.domain.User;
import com.ha.dxl.user.domain.UserRole;
import com.ha.dxl.user.dto.*;
import com.ha.dxl.user.mapper.UserMapper;
import com.ha.dxl.user.repository.UserRepository;
import com.ha.dxl.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of UserService. Handles user CRUD and password management.
 * @see US-002, API-004 to API-009
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username already exists: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Email already exists: " + request.email());
        }
        // Validate role
        UserRole role;
        try {
            role = UserRole.valueOf(request.role());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("VALIDATION_ERROR", "Invalid role: " + request.role());
        }

        User user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(role);
        User saved = userRepository.save(user);
        log.info("User created: username={}, role={}", saved.getUsername(), saved.getRole());
        auditLogService.logAction("SYSTEM", "CREATE_USER", "USER", saved.getId().toString(), null,
                new UserAuditState(saved.getUsername(), saved.getEmail(), saved.getRole().name()), null);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));

        UserAuditState before = new UserAuditState(user.getUsername(), user.getEmail(), user.getRole().name());

        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.username())) {
                throw new ConflictException("Username already exists: " + request.username());
            }
        }
        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.email())) {
                throw new ConflictException("Email already exists: " + request.email());
            }
        }
        if (request.role() != null) {
            try {
                UserRole.valueOf(request.role());
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("VALIDATION_ERROR", "Invalid role: " + request.role());
            }
        }

        userMapper.updateEntity(request, user);
        User saved = userRepository.save(user);
        auditLogService.logAction("SYSTEM", "UPDATE_USER", "USER", saved.getId().toString(), before,
                new UserAuditState(saved.getUsername(), saved.getEmail(), saved.getRole().name()), null);
        return userMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));

        if (user.getRole() == UserRole.ADMIN) {
            long activeAdminCount = userRepository.countByRoleAndIsActiveTrue(UserRole.ADMIN);
            if (activeAdminCount <= 1) {
                throw new BusinessRuleException("BUSINESS_RULE_VIOLATION",
                        "Cannot deactivate the last active ADMIN user");
            }
        }
        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated: id={}", id);
        auditLogService.logAction("SYSTEM", "DEACTIVATE_USER", "USER", id.toString(), null, null, null);
    }

    @Override
    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id.toString()));

        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("BUSINESS_RULE_VIOLATION", "Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        log.info("Password changed for user: id={}", id);
    }

    /**
     * Internal record for audit state (excludes password hash).
     */
    private record UserAuditState(String username, String email, String role) {}
}
