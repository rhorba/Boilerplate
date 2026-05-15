package com.boilerplate.application.mapper;

import com.boilerplate.application.dto.request.PolicyConditionRequest;
import com.boilerplate.application.dto.request.PolicyRequest;
import com.boilerplate.application.dto.response.PolicyConditionResponse;
import com.boilerplate.application.dto.response.PolicyResponse;
import com.boilerplate.domain.model.Policy;
import com.boilerplate.domain.model.PolicyCondition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conditions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "enabled", defaultValue = "true")
    Policy toEntity(PolicyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conditions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "enabled", defaultValue = "true")
    void updateEntity(@MappingTarget Policy policy, PolicyRequest request);

    PolicyResponse toResponse(Policy policy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    PolicyCondition toConditionEntity(PolicyConditionRequest request);

    PolicyConditionResponse toConditionResponse(PolicyCondition condition);
}
