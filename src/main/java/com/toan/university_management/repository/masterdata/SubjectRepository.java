package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByIdAndDeletedFalse(Long id);
    Page<Subject> findAllByDeletedFalse(Pageable pageable);
    List<Subject> findAllByDeletedFalse();
    List<Subject> findAllByIdInAndDeletedFalse(Collection<Long> ids);
    boolean existsBySubjectCodeAndDeletedFalse(String subjectCode);
    boolean existsByIdAndDeletedFalse(Long id);
}
