package com.ha.dxl.auth.service.impl;

import com.ha.dxl.auth.dto.LoginRequest;
import com.ha.dxl.auth.dto.LoginResponse;
import com.ha.dxl.auth.service.AuthService;
import com.ha.dxl.exception.UnauthorizedException;
import com.ha.dxl.security.JwtTokenProvider;
import com.ha.dxl.user.domain.User;
import com.ha.dxl.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService. Handles JWT-based authentication.
 * @see US-001, API-001, API-002, API-003
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is deactivated");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        log.info("User authenticated successfully: username={}", user.getUsername());
        return new LoginResponse(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new UnauthorizedException("Invalid token type");
        }
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is deactivated");
        }

        return new LoginResponse(user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public void logout(String username) {
        log.info("User logged out: username={}", username);
        // Stateless JWT - actual token invalidation is handled by clearing cookies in controller
    }
}
