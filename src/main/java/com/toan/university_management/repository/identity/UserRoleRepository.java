package com.toan.university_management.repository.identity;

import com.toan.university_management.entity.identity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(String userId);

    List<UserRole> findByUserIdIn(Collection<String> userIds);

    List<UserRole> findByRoleId(Long roleId);

    boolean existsByUserIdAndRoleId(String userId, Long roleId);

    void deleteByUserId(String userId);

    void deleteByRoleId(Long roleId);

    void deleteByUserIdAndRoleId(String userId, Long roleId);
}
