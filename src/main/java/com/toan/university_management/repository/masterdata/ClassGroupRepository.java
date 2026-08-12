package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.ClassGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {
    Optional<ClassGroup> findByIdAndDeletedFalse(Long id);
    Page<ClassGroup> findAllByDeletedFalse(Pageable pageable);
    List<ClassGroup> findAllByDeletedFalse();
    List<ClassGroup> findAllByIdInAndDeletedFalse(Collection<Long> ids);
    boolean existsByClassCodeAndDeletedFalse(String classCode);
    boolean existsByIdAndDeletedFalse(Long id);
}
