package com.toan.university_management.service.identity;

import com.toan.university_management.dto.request.identity.PermissionRequest;
import com.toan.university_management.dto.response.identity.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);
    List<PermissionResponse> getAllPermission();
    void deletePermission(String  permission);
}


