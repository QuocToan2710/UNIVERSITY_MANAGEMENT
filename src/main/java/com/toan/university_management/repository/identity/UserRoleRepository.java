package com.toan.university_management.repository.identity;

import com.toan.university_management.entity.identity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);

    List<UserRole> findByUserIdIn(Collection<Long> userIds);

    List<UserRole> findByRoleId(Long roleId);

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    void deleteByUserId(Long userId);

    void deleteByRoleId(Long roleId);

    void deleteByUserIdAndRoleId(Long userId, Long roleId);
}
