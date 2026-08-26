package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.TuitionFee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TuitionFeeRepository extends JpaRepository<TuitionFee, Long> {

    Optional<TuitionFee> findByStudentIdAndSemesterAndAcademicYearAndDeletedFalse(
            Long studentId, String semester, String academicYear);

    List<TuitionFee> findAllByStudentIdAndDeletedFalse(Long studentId);

    List<TuitionFee> findAllBySemesterAndAcademicYearAndDeletedFalse(String semester, String academicYear);

    List<TuitionFee> findAllByDeletedFalse();

    Page<TuitionFee> findAllByDeletedFalse(Pageable pageable);
}