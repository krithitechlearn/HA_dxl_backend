package com.ha.dxl.audit.controller;

import com.ha.dxl.audit.service.impl.AuditLogServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc tests for AuditLogController.
 * @see US-011, API-035, API-036
 */
@WebMvcTest(AuditLogController.class)
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogServiceImpl auditLogService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("US-011: getAuditLogs_withAdminRole_returns200")
    void getAuditLogs_withAdminRole_returns200() throws Exception {
        when(auditLogService.getAuditLogs(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/api/v1/audit-logs"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("US-011: getAuditLogs_withUserRole_returns403")
    void getAuditLogs_withUserRole_returns403() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("US-011: getAuditLogs_withoutAuth_returns401")
    void getAuditLogs_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs"))
                .andExpect(status().isUnauthorized());
    }
}
