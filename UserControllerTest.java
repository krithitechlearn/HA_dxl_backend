package com.ha.dxl.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ha.dxl.exception.ConflictException;
import com.ha.dxl.exception.ResourceNotFoundException;
import com.ha.dxl.user.domain.UserRole;
import com.ha.dxl.user.dto.UserCreateRequest;
import com.ha.dxl.user.dto.UserResponse;
import com.ha.dxl.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc tests for UserController.
 * @see US-002, API-004 to API-009
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("US-002: createUser_withValidData_returns201")
    void createUser_withValidData_returns201() throws Exception {
        UUID userId = UUID.randomUUID();
        UserCreateRequest request = new UserCreateRequest(
                "newuser", "new@example.com", "Password@1", "USER");
        UserResponse response = new UserResponse(userId, "newuser", "new@example.com",
                UserRole.USER, true, OffsetDateTime.now(), OffsetDateTime.now());

        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("US-002: createUser_withUserRole_returns403")
    void createUser_withUserRole_returns403() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "newuser", "new@example.com", "Password@1", "USER");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("US-002: createUser_whenUsernameConflict_returns409")
    void createUser_whenUsernameConflict_returns409() throws Exception {
        UserCreateRequest request = new UserCreateRequest(
                "admin", "admin@example.com", "Password@1", "ADMIN");
        when(userService.createUser(any())).thenThrow(new ConflictException("Username already exists"));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("CONFLICT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("US-002: getUserById_whenNotFound_returns404")
    void getUserById_whenNotFound_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(userService.getUserById(unknownId))
                .thenThrow(new ResourceNotFoundException("User", unknownId.toString()));

        mockMvc.perform(get("/api/v1/users/" + unknownId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("US-002: getAllUsers_withoutAuth_returns401")
    void getAllUsers_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());
    }
}
