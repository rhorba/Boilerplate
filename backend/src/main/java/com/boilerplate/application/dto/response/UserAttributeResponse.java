package com.boilerplate.application.dto.response;

public record UserAttributeResponse(
    Long id,
    String attributeKey,
    String attributeValue
) { }
