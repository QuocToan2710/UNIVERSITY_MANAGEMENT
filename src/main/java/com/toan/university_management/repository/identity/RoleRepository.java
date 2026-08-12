package com.toan.university_management.repository.identity;

import com.toan.university_management.entity.identity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByRoleCode(String roleCode);

    Optional<Role> findByName(String name);

    List<Role> findAllByRoleCodeIn(Collection<String> roleCodes);
}
