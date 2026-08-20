package com.toan.university_management.repository.identity;

import com.toan.university_management.entity.identity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);

    Page<User> findAll(Pageable pageable);

    List<User> findAllByDeletedFalse();

    List<User> findAllByIdIn(Collection<String> ids);
}
