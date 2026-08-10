package com.toan.university_management.mapper.identity;


import com.toan.university_management.dto.request.identity.PermissionRequest;
import com.toan.university_management.dto.request.identity.UserRequest;
import com.toan.university_management.dto.response.identity.PermissionResponse;
import com.toan.university_management.dto.response.identity.UserResponse;
import com.toan.university_management.entity.identity.Permission;
import com.toan.university_management.entity.identity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}


