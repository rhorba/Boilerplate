package com.boilerplate.presentation.controller;

import com.boilerplate.application.dto.request.UserAttributeRequest;
import com.boilerplate.application.dto.response.UserAttributeResponse;
import com.boilerplate.application.service.UserAttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/attributes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Attributes", description = "Manage ABAC subject attributes for users")
public class UserAttributeController {

    private final UserAttributeService userAttributeService;

    @GetMapping
    @PreAuthorize("@abacEvaluator.hasPermission(authentication, 'USER', 'MANAGE', #userId)")
    @Operation(summary = "Get all attributes for a user")
    public ResponseEntity<List<UserAttributeResponse>> getAttributes(@PathVariable Long userId) {
        return ResponseEntity.ok(userAttributeService.getAttributesForUser(userId));
    }

    @PutMapping
    @PreAuthorize("@abacEvaluator.hasPermission(authentication, 'USER', 'MANAGE', #userId)")
    @Operation(summary = "Set (create or update) a user attribute")
    public ResponseEntity<UserAttributeResponse> setAttribute(
        @PathVariable Long userId,
        @Valid @RequestBody UserAttributeRequest request
    ) {
        return ResponseEntity.ok(userAttributeService.setUserAttribute(userId, request));
    }

    @DeleteMapping("/{attributeKey}")
    @PreAuthorize("@abacEvaluator.hasPermission(authentication, 'USER', 'MANAGE', #userId)")
    @Operation(summary = "Delete a user attribute")
    public ResponseEntity<Void> deleteAttribute(
        @PathVariable Long userId,
        @PathVariable String attributeKey
    ) {
        userAttributeService.deleteUserAttribute(userId, attributeKey);
        return ResponseEntity.noContent().build();
    }
}
