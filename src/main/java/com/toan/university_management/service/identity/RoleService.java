package com.toan.university_management.service.identity;

import com.toan.university_management.dto.request.identity.RoleRequest;
import com.toan.university_management.dto.response.identity.RoleResponse;

import java.util.List;
import java.util.Set;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    List<RoleResponse> getAllRole();
    RoleResponse updateRolePermissions(String roleName, Set<String> permissions);
    void deleteRole(String  role);
}


