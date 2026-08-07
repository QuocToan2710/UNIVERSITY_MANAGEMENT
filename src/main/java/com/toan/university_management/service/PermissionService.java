package com.toan.university_management.service;

import com.toan.university_management.dto.request.PermissionRequest;
import com.toan.university_management.dto.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
    PermissionResponse createPermission(PermissionRequest request);
    List<PermissionResponse> getAllPermission();
    void deletePermission(String  permission);
}
