package com.toan.university_management.service.identity;

import com.toan.university_management.dto.request.identity.RoleRequest;
import com.toan.university_management.dto.response.identity.PermissionResponse;
import com.toan.university_management.dto.response.identity.RoleResponse;
import com.toan.university_management.entity.identity.Role;
import com.toan.university_management.entity.identity.RolePermission;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.identity.RoleMapper;
import com.toan.university_management.repository.identity.PermissionRepository;
import com.toan.university_management.repository.identity.RolePermissionRepository;
import com.toan.university_management.repository.identity.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {
    RoleRepository roleRepository;
    RolePermissionRepository rolePermissionRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public RoleResponse createRole(RoleRequest request) {
        var role = roleMapper.toRole(request);
        if (role.getRoleCode() == null || role.getRoleCode().isBlank()) {
            role.setRoleCode("ROLE_" + request.getName().toUpperCase().replace(" ", "_"));
        }
        role = roleRepository.save(role);

        if (request.getPermissions() != null) {
            for (String permCode : request.getPermissions()) {
                rolePermissionRepository.save(RolePermission.builder()
                        .roleCode(role.getRoleCode())
                        .permissionCode(permCode)
                        .build());
            }
        }

        return enrichRoleResponse(role);
    }

    @Override
    public List<RoleResponse> getAllRole() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(this::enrichRoleResponse).toList();
    }

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public RoleResponse updateRolePermissions(String roleName, Set<String> permissions) {
        var role = roleRepository.findByName(roleName)
                .or(() -> roleRepository.findByRoleCode(roleName))
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        rolePermissionRepository.deleteByRoleCode(role.getRoleCode());
        if (permissions != null) {
            for (String permCode : permissions) {
                rolePermissionRepository.save(RolePermission.builder()
                        .roleCode(role.getRoleCode())
                        .permissionCode(permCode)
                        .build());
            }
        }

        return enrichRoleResponse(role);
    }

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public void deleteRole(String roleName) {
        var role = roleRepository.findByName(roleName)
                .or(() -> roleRepository.findByRoleCode(roleName))
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        rolePermissionRepository.deleteByRoleCode(role.getRoleCode());
        roleRepository.deleteById(role.getId());
    }

    private RoleResponse enrichRoleResponse(Role role) {
        RoleResponse response = roleMapper.toRoleResponse(role);
        List<RolePermission> rps = rolePermissionRepository.findByRoleCode(role.getRoleCode());
        Set<PermissionResponse> perms = rps.stream()
                .map(rp -> PermissionResponse.builder().permissionCode(rp.getPermissionCode()).name(rp.getPermissionCode()).build())
                .collect(Collectors.toSet());
        response.setPermissions(perms);
        return response;
    }
}
