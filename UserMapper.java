package com.ha.dxl.user.mapper;

import com.ha.dxl.user.domain.User;
import com.ha.dxl.user.domain.UserRole;
import com.ha.dxl.user.dto.UserCreateRequest;
import com.ha.dxl.user.dto.UserResponse;
import com.ha.dxl.user.dto.UserUpdateRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for User entity and DTOs.
 * @see US-002
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "role", expression = "java(com.ha.dxl.user.domain.UserRole.valueOf(request.role()))")
    User toEntity(UserCreateRequest request);

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "role", expression = "java(request.role() != null ? com.ha.dxl.user.domain.UserRole.valueOf(request.role()) : user.getRole())")
    @Mapping(target = "username", expression = "java(request.username() != null ? request.username() : user.getUsername())")
    @Mapping(target = "email", expression = "java(request.email() != null ? request.email() : user.getEmail())")
    void updateEntity(UserUpdateRequest request, @MappingTarget User user);
}
