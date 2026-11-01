package com.ha.dxl.user.service;

import com.ha.dxl.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for user management operations.
 * @see US-002
 */
public interface UserService {

    /** @see API-004 */
    UserResponse createUser(UserCreateRequest request);

    /** @see API-006 */
    UserResponse getUserById(UUID id);

    /** @see API-005 */
    Page<UserResponse> getAllUsers(Pageable pageable);

    /** @see API-007 */
    UserResponse updateUser(UUID id, UserUpdateRequest request);

    /** @see API-008 */
    void deactivateUser(UUID id);

    /** @see API-009 */
    void changePassword(UUID id, ChangePasswordRequest request);
}
