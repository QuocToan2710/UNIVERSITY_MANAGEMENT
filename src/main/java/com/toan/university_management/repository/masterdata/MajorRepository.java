package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Major;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findByIdAndDeletedFalse(Long id);
    Page<Major> findAllByDeletedFalse(Pageable pageable);
    List<Major> findAllByDeletedFalse();
    List<Major> findAllByIdInAndDeletedFalse(Collection<Long> ids);
    boolean existsByMajorCodeAndDeletedFalse(String majorCode);
    boolean existsByIdAndDeletedFalse(Long id);
}
