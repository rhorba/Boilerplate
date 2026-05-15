package com.boilerplate.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserAttributeRequest(
    @NotBlank String attributeKey,
    @NotBlank String attributeValue
) { }
