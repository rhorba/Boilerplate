package com.boilerplate.presentation.controller;

import com.boilerplate.application.dto.request.PolicyRequest;
import com.boilerplate.application.dto.response.PolicyResponse;
import com.boilerplate.application.service.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Policies", description = "ABAC policy management endpoints")
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    @PreAuthorize("@abacEvaluator.hasPermission(authentication, 'POLICY', 'MANAGE')")
    @Operation(summary = "Get all policies")
    public ResponseEntity<List<PolicyResponse>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@abacEvaluator.hasPermission(authentication, 'POLICY', 'MANAGE')")
    @Operation(summary = "Get policy by ID")
    public ResponseEntity<PolicyResponse> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @PostMapping
    @PreAuthorize("@abacEvaluator.hasPermission(authentication, 'POLICY', 'MANAGE')")
    @Operation(summary = "Create policy")
    public ResponseEntity<PolicyResponse> createPolicy(@Valid @RequestBody PolicyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.createPolicy(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@abacEvaluator.hasPermission(authentication, 'POLICY', 'MANAGE')")
    @Operation(summary = "Update policy")
    public ResponseEntity<PolicyResponse> updatePolicy(
        @PathVariable Long id,
        @Valid @RequestBody PolicyRequest request
    ) {
        return ResponseEntity.ok(policyService.updatePolicy(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@abacEvaluator.hasPermission(authentication, 'POLICY', 'MANAGE')")
    @Operation(summary = "Delete policy")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        policyService.deletePolicy(id);
        return ResponseEntity.noContent().build();
    }
}
