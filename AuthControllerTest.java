package com.ha.dxl.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ha.dxl.auth.dto.LoginRequest;
import com.ha.dxl.auth.dto.LoginResponse;
import com.ha.dxl.auth.service.AuthService;
import com.ha.dxl.config.JwtConfig;
import com.ha.dxl.security.JwtTokenProvider;
import com.ha.dxl.exception.UnauthorizedException;
import com.ha.dxl.user.domain.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc tests for AuthController.
 * @see US-001, API-001, API-002, API-003
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtConfig jwtConfig;

    @Test
    @DisplayName("US-001: login_withValidCredentials_returns200")
    void login_withValidCredentials_returns200() throws Exception {
        LoginRequest request = new LoginRequest("admin", "Admin@1234");
        LoginResponse response = new LoginResponse(UUID.randomUUID(), "admin", UserRole.ADMIN);

        when(authService.login(any())).thenReturn(response);
        when(userDetailsService.loadUserByUsername(any())).thenReturn(
                org.springframework.security.core.userdetails.User.builder()
                        .username("admin").password("hash")
                        .roles("ADMIN").build());
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");
        when(jwtConfig.getAccessTokenExpiryMs()).thenReturn(900000L);
        when(jwtConfig.getRefreshTokenExpiryMs()).thenReturn(604800000L);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @DisplayName("US-001: login_withInvalidCredentials_returns401")
    void login_withInvalidCredentials_returns401() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");
        when(authService.login(any())).thenThrow(new UnauthorizedException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_FAILED"));
    }

    @Test
    @DisplayName("US-001: login_withBlankUsername_returns400")
    void login_withBlankUsername_returns400() throws Exception {
        LoginRequest request = new LoginRequest("", "password");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser
    @DisplayName("US-001: logout_whenAuthenticated_returns204")
    void logout_whenAuthenticated_returns204() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isNoContent());
    }
}
