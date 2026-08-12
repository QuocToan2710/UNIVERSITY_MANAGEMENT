package com.toan.university_management.repository.identity;

import com.toan.university_management.entity.identity.Permission;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    Optional<Permission> findByPermissionCode(String permissionCode);

    Optional<Permission> findByMethodAndEndpoint(String method, String endpoint);

    @Cacheable(value = "publicPermissions")
    List<Permission> findByIsPublicTrue();

    List<Permission> findAllByPermissionCodeIn(Collection<String> permissionCodes);
}
