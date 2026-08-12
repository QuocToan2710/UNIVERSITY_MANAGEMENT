package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.SubjectClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubjectClassRepository extends JpaRepository<SubjectClass, Long> {
    Optional<SubjectClass> findByIdAndDeletedFalse(Long id);
    Page<SubjectClass> findAllByDeletedFalse(Pageable pageable);
    List<SubjectClass> findAllByDeletedFalse();
    List<SubjectClass> findAllByIdInAndDeletedFalse(Collection<Long> ids);
    boolean existsBySubjectClassCodeAndDeletedFalse(String subjectClassCode);
    boolean existsByIdAndDeletedFalse(Long id);
}
