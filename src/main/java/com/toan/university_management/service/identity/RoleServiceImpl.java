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
    com.toan.university_management.repository.identity.UserRoleRepository userRoleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;
    com.toan.university_management.mapper.identity.PermissionMapper permissionMapper;

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public RoleResponse createRole(RoleRequest request) {
        var role = roleMapper.toRole(request);
        if (role.getRoleCode() == null || role.getRoleCode().isBlank()) {
            role.setRoleCode("ROLE_" + request.getName().toUpperCase().replace(" ", "_"));
        }
        role = roleRepository.save(role);
        final Long roleId = role.getId();

        if (request.getPermissions() != null) {
            for (String permKey : request.getPermissions()) {
                resolvePermission(permKey).ifPresent(perm -> {
                    rolePermissionRepository.save(RolePermission.builder()
                            .roleId(roleId)
                            .permissionId(perm.getId())
                            .build());
                });
            }
        }

        return enrichRoleResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRole() {
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) return Collections.emptyList();

        // Batch: lấy tất cả rolePermissions + permissions 1 lần thay vì N lần
        Set<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
        List<RolePermission> allRps = rolePermissionRepository.findByRoleIdIn(roleIds);
        Set<Long> permIds = allRps.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());
        Map<Long, com.toan.university_management.entity.identity.Permission> permMap = permIds.isEmpty()
                ? Collections.emptyMap()
                : permissionRepository.findAllByIdIn(permIds).stream()
                        .collect(Collectors.toMap(
                                com.toan.university_management.entity.identity.Permission::getId,
                                p -> p));

        // Nhóm permissions theo roleId
        Map<Long, Set<PermissionResponse>> rolePermMap = new HashMap<>();
        for (RolePermission rp : allRps) {
            com.toan.university_management.entity.identity.Permission perm = permMap.get(rp.getPermissionId());
            if (perm != null) {
                rolePermMap.computeIfAbsent(rp.getRoleId(), k -> new HashSet<>())
                        .add(permissionMapper.toPermissionResponse(perm));
            }
        }

        return roles.stream().map(role -> {
            RoleResponse res = roleMapper.toRoleResponse(role);
            res.setPermissions(rolePermMap.getOrDefault(role.getId(), Collections.emptySet()));
            return res;
        }).toList();
    }

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public RoleResponse updateRolePermissions(String roleIdentifier, Set<String> permissions) {
        var role = findRoleByIdentifier(roleIdentifier);
        final Long targetRoleId = role.getId();

        rolePermissionRepository.deleteByRoleId(targetRoleId);
        if (permissions != null) {
            for (String permKey : permissions) {
                resolvePermission(permKey).ifPresent(perm -> {
                    rolePermissionRepository.save(RolePermission.builder()
                            .roleId(targetRoleId)
                            .permissionId(perm.getId())
                            .build());
                });
            }
        }

        return enrichRoleResponse(role);
    }

    @Override
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public void deleteRole(String roleIdentifier) {
        var role = findRoleByIdentifier(roleIdentifier);

        rolePermissionRepository.deleteByRoleId(role.getId());
        userRoleRepository.deleteByRoleId(role.getId());
        roleRepository.deleteById(role.getId());
    }

    private Role findRoleByIdentifier(String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            Optional<Role> roleOpt = roleRepository.findById(id);
            if (roleOpt.isPresent()) return roleOpt.get();
        } catch (NumberFormatException ignored) {}

        return roleRepository.findByRoleCode(identifier)
                .or(() -> roleRepository.findByName(identifier))
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
    }

    private Optional<com.toan.university_management.entity.identity.Permission> resolvePermission(String permKey) {
        try {
            Long pId = Long.parseLong(permKey);
            Optional<com.toan.university_management.entity.identity.Permission> permOpt = permissionRepository.findById(pId);
            if (permOpt.isPresent()) return permOpt;
        } catch (NumberFormatException ignored) {}

        return permissionRepository.findByPermissionCode(permKey)
                .or(() -> permissionRepository.findByName(permKey));
    }

    private RoleResponse enrichRoleResponse(Role role) {
        RoleResponse response = roleMapper.toRoleResponse(role);
        List<RolePermission> rps = rolePermissionRepository.findByRoleId(role.getId());
        Set<Long> permIds = rps.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet());
        if (!permIds.isEmpty()) {
            Set<PermissionResponse> perms = permissionRepository.findAllByIdIn(permIds).stream()
                    .map(permissionMapper::toPermissionResponse)
                    .collect(Collectors.toSet());
            response.setPermissions(perms);
        } else {
            response.setPermissions(Collections.emptySet());
        }
        return response;
    }
}
