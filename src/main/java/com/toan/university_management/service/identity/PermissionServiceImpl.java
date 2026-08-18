package com.toan.university_management.service.identity;

import com.toan.university_management.dto.request.identity.PermissionRequest;
import com.toan.university_management.dto.response.identity.PermissionResponse;
import com.toan.university_management.entity.identity.Permission;
import com.toan.university_management.mapper.identity.PermissionMapper;
import com.toan.university_management.repository.identity.PermissionRepository;
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
    com.toan.university_management.repository.identity.RolePermissionRepository rolePermissionRepository;
    PermissionMapper permissionMapper;

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        if (permission.getPermissionCode() == null || permission.getPermissionCode().isBlank()) {
            permission.setPermissionCode("PERM_" + permission.getName().toUpperCase().replace(" ", "_"));
        }
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    public List<PermissionResponse> getAllPermission() {
        var permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public void deletePermission(String permissionIdentifier) {
        Long permId = null;
        try {
            permId = Long.parseLong(permissionIdentifier);
        } catch (NumberFormatException ignored) {
            var permOpt = permissionRepository.findByPermissionCode(permissionIdentifier)
                    .or(() -> permissionRepository.findByName(permissionIdentifier));
            if (permOpt.isPresent()) {
                permId = permOpt.get().getId();
            }
        }

        if (permId != null) {
            rolePermissionRepository.deleteByPermissionId(permId);
            permissionRepository.deleteById(permId);
        }
    }
}
