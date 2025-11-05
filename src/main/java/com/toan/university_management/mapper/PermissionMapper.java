package com.toan.university_management.mapper;


import com.toan.university_management.dto.request.PermissionRequest;
import com.toan.university_management.dto.request.UserRequest;
import com.toan.university_management.dto.response.PermissionResponse;
import com.toan.university_management.dto.response.UserResponse;
import com.toan.university_management.entity.Permission;
import com.toan.university_management.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
