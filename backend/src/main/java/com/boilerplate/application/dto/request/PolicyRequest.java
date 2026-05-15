package com.boilerplate.application.dto.request;

import com.boilerplate.domain.model.Policy.PolicyAction;
import com.boilerplate.domain.model.Policy.PolicyEffect;
import com.boilerplate.domain.model.Policy.PolicyResource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PolicyRequest(
    @NotBlank String name,
    String description,
    @NotNull PolicyEffect effect,
    @NotNull PolicyResource resource,
    @NotNull PolicyAction action,
    Boolean enabled,
    @Valid List<PolicyConditionRequest> conditions
) { }
