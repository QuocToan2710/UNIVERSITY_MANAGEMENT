package com.toan.university_management.repository.identity;

import com.toan.university_management.entity.identity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByUsernameAndDeletedFalse(String username);

    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndDeletedFalse(String username);

    Optional<User> findByUsernameIgnoreCase(String username);
    Optional<User> findByUsernameIgnoreCaseAndDeletedFalse(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndDeletedFalse(String email);

    boolean existsByEmail(String email);
    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByEmailAndIdNotAndDeletedFalse(String email, Long id);

    Optional<User> findByUserCode(String userCode);
    Optional<User> findByUserCodeAndDeletedFalse(String userCode);

    Optional<User> findByUserCodeIgnoreCase(String userCode);
    Optional<User> findByUserCodeIgnoreCaseAndDeletedFalse(String userCode);

    Page<User> findAll(Pageable pageable);

    List<User> findAllByDeletedFalse();

    Page<User> findAllByDeletedFalse(Pageable pageable);

    Optional<User> findByIdAndDeletedFalse(Long id);

    boolean existsByIdAndDeletedFalse(Long id);

    List<User> findAllByIdIn(Collection<Long> ids);
}
