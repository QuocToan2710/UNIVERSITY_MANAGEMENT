package com.toan.university_management.service.implement;


import com.toan.university_management.dto.request.PermissionRequest;
import com.toan.university_management.dto.response.PermissionResponse;
import com.toan.university_management.entity.Permission;
import com.toan.university_management.mapper.PermissionMapper;
import com.toan.university_management.repository.PermissionRepository;
import com.toan.university_management.service.PermissonService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionSeviceImpl implements PermissonService{
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }
    @Override
    public List<PermissionResponse> getAllPermission() {
        var permission = permissionRepository.findAll();
        return permission.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @Override
    public void deletePermission(String permission) {
        permissionRepository.deleteById(permission);
    }
}
