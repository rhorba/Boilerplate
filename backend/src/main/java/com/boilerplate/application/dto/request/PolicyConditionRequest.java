package com.boilerplate.application.dto.request;

import com.boilerplate.domain.model.PolicyCondition.ConditionOperator;
import com.boilerplate.domain.model.PolicyCondition.ConditionSubject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PolicyConditionRequest(
    @NotNull ConditionSubject subject,
    @NotBlank String attributeKey,
    @NotNull ConditionOperator operator,
    @NotBlank String attributeValue
) { }
