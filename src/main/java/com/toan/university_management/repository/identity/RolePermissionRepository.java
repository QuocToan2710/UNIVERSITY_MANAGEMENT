package com.toan.university_management.repository.identity;

import com.toan.university_management.entity.identity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Long roleId);

    List<RolePermission> findByRoleIdIn(Collection<Long> roleIds);

    List<RolePermission> findByPermissionId(Long permissionId);

    void deleteByRoleId(Long roleId);

    void deleteByPermissionId(Long permissionId);

    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
