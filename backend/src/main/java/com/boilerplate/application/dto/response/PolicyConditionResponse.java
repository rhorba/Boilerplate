package com.boilerplate.application.dto.response;

import com.boilerplate.domain.model.PolicyCondition.ConditionOperator;
import com.boilerplate.domain.model.PolicyCondition.ConditionSubject;

public record PolicyConditionResponse(
    Long id,
    ConditionSubject subject,
    String attributeKey,
    ConditionOperator operator,
    String attributeValue
) { }
