package com.toan.university_management.mapper.identity;

import com.toan.university_management.dto.request.identity.RoleRequest;
import com.toan.university_management.dto.response.identity.RoleResponse;
import com.toan.university_management.entity.identity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest request);

    @Mapping(target = "permissions", ignore = true)
    RoleResponse toRoleResponse(Role role);
}
