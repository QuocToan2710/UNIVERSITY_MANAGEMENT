package com.toan.university_management.repository.identity;

import com.toan.university_management.entity.identity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {
    List<RolePermission> findByRoleCode(String roleCode);

    List<RolePermission> findByRoleCodeIn(Collection<String> roleCodes);

    void deleteByRoleCode(String roleCode);
}
