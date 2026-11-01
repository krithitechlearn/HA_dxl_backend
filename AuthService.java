package com.ha.dxl.auth.service;

import com.ha.dxl.auth.dto.LoginRequest;
import com.ha.dxl.auth.dto.LoginResponse;

/**
 * Service interface for authentication operations.
 * @see US-001
 */
public interface AuthService {

    /**
     * Authenticates a user and returns user info. Tokens are set in cookies.
     * @see API-001
     */
    LoginResponse login(LoginRequest request);

    /**
     * Refreshes the access token using the refresh token.
     * @see API-002
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * Logs out the current user (stateless - cookie clearing handled by controller).
     * @see API-003
     */
    void logout(String username);
}
