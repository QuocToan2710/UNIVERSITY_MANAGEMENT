package com.toan.university_management.service;

import com.toan.university_management.dto.request.RoleRequest;
import com.toan.university_management.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest request);
    List<RoleResponse> getAllRole();
    void deleteRole(String  role);
}
