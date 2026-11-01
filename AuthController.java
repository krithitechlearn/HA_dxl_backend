package com.ha.dxl.auth.controller;

import com.ha.dxl.auth.dto.LoginRequest;
import com.ha.dxl.auth.dto.LoginResponse;
import com.ha.dxl.auth.dto.RefreshTokenRequest;
import com.ha.dxl.auth.service.AuthService;
import com.ha.dxl.config.JwtConfig;
import com.ha.dxl.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * REST controller for authentication operations.
 * @see US-001, API-001, API-002, API-003
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "JWT authentication endpoints")
@RequiredArgsConstructor
public class AuthController {

    private static final String ACCESS_TOKEN_COOKIE = "ha_access_token";
    private static final String REFRESH_TOKEN_COOKIE = "ha_refresh_token";

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final JwtConfig jwtConfig;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "API-001: Authenticate user and return JWT in HttpOnly cookies")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginResponse.username());
        setAuthCookies(response, userDetails);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "API-002: Refresh access token using refresh token cookie")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request,
                                                       HttpServletResponse response) {
        String refreshToken = extractCookieValue(request, REFRESH_TOKEN_COOKIE);
        LoginResponse loginResponse = authService.refreshToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginResponse.username());
        setAuthCookies(response, userDetails);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "API-003: Invalidate session and clear cookies")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        clearAuthCookies(response);
        return ResponseEntity.noContent().build();
    }

    private void setAuthCookies(HttpServletResponse response, UserDetails userDetails) {
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE, accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (jwtConfig.getAccessTokenExpiryMs() / 1000));
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE, refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/v1/auth/refresh");
        refreshCookie.setMaxAge((int) (jwtConfig.getRefreshTokenExpiryMs() / 1000));
        response.addCookie(refreshCookie);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie(ACCESS_TOKEN_COOKIE, "");
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie(REFRESH_TOKEN_COOKIE, "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/v1/auth/refresh");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
    }

    private String extractCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
