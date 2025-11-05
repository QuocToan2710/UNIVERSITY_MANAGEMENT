package com.toan.university_management.mapper;


import com.toan.university_management.dto.request.RoleRequest;
import com.toan.university_management.dto.response.RoleResponse;
import com.toan.university_management.entity.Permission;
import com.toan.university_management.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "mapPermissionsToNames")
    RoleResponse toRoleResponse(Role role);

    @Named("mapPermissionsToNames")
    default Set<String> mapPermissionsToNames(Set<Permission> permissions) {
        if (permissions == null) return Set.of();
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

}
