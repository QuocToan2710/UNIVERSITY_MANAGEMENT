package com.toan.university_management.service.identity;


import com.toan.university_management.dto.request.identity.PermissionRequest;
import com.toan.university_management.dto.response.identity.PermissionResponse;
import com.toan.university_management.entity.identity.Permission;
import com.toan.university_management.mapper.identity.PermissionMapper;
import com.toan.university_management.repository.identity.PermissionRepository;
import com.toan.university_management.service.identity.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class PermissionServiceImpl implements PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
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
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public void deletePermission(String permission) {
        permissionRepository.deleteById(permission);
    }
}


