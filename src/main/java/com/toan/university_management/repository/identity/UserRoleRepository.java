package com.toan.university_management.repository.identity;

import com.toan.university_management.entity.identity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    List<UserRole> findByUserId(String userId);

    List<UserRole> findByUserIdIn(Collection<String> userIds);

    boolean existsByUserIdAndRoleCode(String userId, String roleCode);

    void deleteByUserId(String userId);

    void deleteByUserIdAndRoleCode(String userId, String roleCode);
}
