package com.ha.dxl.device.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ha.dxl.device.domain.DeviceStatus;
import com.ha.dxl.device.domain.DeviceType;
import com.ha.dxl.device.dto.DeviceCreateRequest;
import com.ha.dxl.device.dto.DeviceResponse;
import com.ha.dxl.device.service.DeviceService;
import com.ha.dxl.exception.ResourceNotFoundException;
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
 * MockMvc tests for DeviceController.
 * @see US-003, US-004, API-015 to API-019
 */
@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeviceService deviceService;

    private DeviceResponse buildDeviceResponse(UUID id) {
        return new DeviceResponse(id, "Test Light", DeviceType.LIGHT, DeviceStatus.OFFLINE,
                "Philips", "HUE-001", "1.0.0", null, null, null, null,
                0, true, null, OffsetDateTime.now(), OffsetDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("US-004: registerDevice_withValidData_returns201")
    void registerDevice_withValidData_returns201() throws Exception {
        UUID deviceId = UUID.randomUUID();
        DeviceCreateRequest request = new DeviceCreateRequest(
                "Test Light", "LIGHT", "Philips", "HUE-001", "1.0.0", null, null, null);
        when(deviceService.registerDevice(any())).thenReturn(buildDeviceResponse(deviceId));

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Light"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("US-004: registerDevice_withUserRole_returns403")
    void registerDevice_withUserRole_returns403() throws Exception {
        DeviceCreateRequest request = new DeviceCreateRequest(
                "Test Light", "LIGHT", null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("US-004: registerDevice_withoutAuth_returns401")
    void registerDevice_withoutAuth_returns401() throws Exception {
        DeviceCreateRequest request = new DeviceCreateRequest(
                "Test Light", "LIGHT", null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("US-004: getDeviceById_whenNotFound_returns404")
    void getDeviceById_whenNotFound_returns404() throws Exception {
        UUID unknownId = UUID.randomUUID();
        when(deviceService.getDeviceById(unknownId))
                .thenThrow(new ResourceNotFoundException("Device", unknownId.toString()));

        mockMvc.perform(get("/api/v1/devices/" + unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("US-004: registerDevice_withBlankName_returns400")
    void registerDevice_withBlankName_returns400() throws Exception {
        DeviceCreateRequest request = new DeviceCreateRequest(
                "", "LIGHT", null, null, null, null, null, null);

        mockMvc.perform(post("/api/v1/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }
}
