package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByIdAndDeletedFalse(Long id);
    Page<Enrollment> findAllByDeletedFalse(Pageable pageable);
    List<Enrollment> findAllByDeletedFalse();
    List<Enrollment> findAllByStudentIdAndDeletedFalse(Long studentId);
    boolean existsByStudentIdAndSubjectClassIdAndDeletedFalse(Long studentId, Long subjectClassId);
    boolean existsByIdAndDeletedFalse(Long id);
}
