package com.ha.dxl.automation.controller;

import com.ha.dxl.automation.dto.*;
import com.ha.dxl.automation.service.AutomationExecutionService;
import com.ha.dxl.automation.service.AutomationRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

/**
 * REST controller for automation rule management.
 * @see US-009, US-010, API-027 to API-034
 */
@RestController
@RequestMapping("/api/v1/automations")
@Tag(name = "Automations", description = "Automation rule management endpoints")
@RequiredArgsConstructor
public class AutomationRuleController {

    private final AutomationRuleService automationRuleService;
    private final AutomationExecutionService automationExecutionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Create automation rule", description = "API-027: Create a new automation rule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rule created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<AutomationRuleResponse> createRule(
            @Valid @RequestBody AutomationRuleCreateRequest request) {
        AutomationRuleResponse response = automationRuleService.createRule(request);
        return ResponseEntity.created(URI.create("/api/v1/automations/" + response.id())).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "List automation rules", description = "API-028: Get paginated list of automation rules")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rules retrieved"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Page<AutomationRuleResponse>> getAllRules(
            @RequestParam(required = false) UUID ownerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(automationRuleService.getAllRules(ownerId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Get automation rule", description = "API-029: Get automation rule details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rule found"),
            @ApiResponse(responseCode = "404", description = "Rule not found")
    })
    public ResponseEntity<AutomationRuleResponse> getRuleById(@PathVariable UUID id) {
        return ResponseEntity.ok(automationRuleService.getRuleById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Update automation rule", description = "API-030: Update an automation rule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rule updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Rule not found"),
            @ApiResponse(responseCode = "409", description = "Optimistic lock conflict")
    })
    public ResponseEntity<AutomationRuleResponse> updateRule(
            @PathVariable UUID id,
            @Valid @RequestBody AutomationRuleUpdateRequest request) {
        return ResponseEntity.ok(automationRuleService.updateRule(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Delete automation rule", description = "API-031: Delete an automation rule")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Rule deleted"),
            @ApiResponse(responseCode = "404", description = "Rule not found")
    })
    public ResponseEntity<Void> deleteRule(@PathVariable UUID id) {
        automationRuleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Toggle automation rule", description = "API-032: Enable or disable an automation rule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rule toggled"),
            @ApiResponse(responseCode = "404", description = "Rule not found")
    })
    public ResponseEntity<AutomationRuleResponse> toggleRule(
            @PathVariable UUID id,
            @Valid @RequestBody AutomationToggleRequest request) {
        return ResponseEntity.ok(automationRuleService.toggleRule(id, request.enabled()));
    }

    @PostMapping("/{id}/trigger")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Manually trigger rule", description = "API-033: Manually trigger an automation rule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rule triggered"),
            @ApiResponse(responseCode = "404", description = "Rule not found"),
            @ApiResponse(responseCode = "422", description = "Rule is disabled")
    })
    public ResponseEntity<AutomationExecutionLogResponse> triggerManually(@PathVariable UUID id) {
        AutomationExecutionLogResponse response = automationRuleService.triggerManually(id);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}/logs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @Operation(summary = "Get execution logs", description = "API-034: Get execution logs for an automation rule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logs retrieved"),
            @ApiResponse(responseCode = "404", description = "Rule not found")
    })
    public ResponseEntity<Page<AutomationExecutionLogResponse>> getExecutionLogs(
            @PathVariable UUID id,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(automationExecutionService.getExecutionLogs(id, pageable));
    }
}
