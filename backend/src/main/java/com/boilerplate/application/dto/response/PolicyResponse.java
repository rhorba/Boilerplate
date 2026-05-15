package com.boilerplate.application.dto.response;

import com.boilerplate.domain.model.Policy.PolicyAction;
import com.boilerplate.domain.model.Policy.PolicyEffect;
import com.boilerplate.domain.model.Policy.PolicyResource;

import java.time.LocalDateTime;
import java.util.Set;

public record PolicyResponse(
    Long id,
    String name,
    String description,
    PolicyEffect effect,
    PolicyResource resource,
    PolicyAction action,
    Boolean enabled,
    Set<PolicyConditionResponse> conditions,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) { }
