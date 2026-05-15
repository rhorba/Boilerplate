package com.boilerplate.application.mapper;

import com.boilerplate.application.dto.request.UserAttributeRequest;
import com.boilerplate.application.dto.response.UserAttributeResponse;
import com.boilerplate.domain.model.UserAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserAttributeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    UserAttribute toEntity(UserAttributeRequest request);

    UserAttributeResponse toResponse(UserAttribute attribute);
}
