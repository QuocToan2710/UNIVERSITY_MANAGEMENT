package com.toan.university_management.service.identity;


import com.toan.university_management.dto.request.identity.RoleRequest;
import com.toan.university_management.dto.response.identity.RoleResponse;
import com.toan.university_management.mapper.identity.RoleMapper;
import com.toan.university_management.repository.identity.PermissionRepository;
import com.toan.university_management.repository.identity.RoleRepository;
import com.toan.university_management.service.identity.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;


    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public RoleResponse createRole(RoleRequest request) {
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @Override
    public List<RoleResponse> getAllRole() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public RoleResponse updateRolePermissions(String roleName, java.util.Set<String> permissions) {
        var role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new com.toan.university_management.exception.AppException(com.toan.university_management.exception.ErrorCode.ROLE_NOT_FOUND));

        var permissionEntities = permissionRepository.findAllById(permissions);
        role.setPermissions(new HashSet<>(permissionEntities));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public void deleteRole(String role) {
        roleRepository.deleteById(role);
    }
}


