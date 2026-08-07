package com.toan.university_management.service;

import com.toan.university_management.dto.request.RoleRequest;
import com.toan.university_management.dto.response.RoleResponse;

import java.util.List;
import java.util.Set;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    List<RoleResponse> getAllRole();
    RoleResponse updateRolePermissions(String roleName, Set<String> permissions);
    void deleteRole(String  role);
}
