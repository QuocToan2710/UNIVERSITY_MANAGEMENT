package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.ExamSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
    Optional<ExamSchedule> findByIdAndDeletedFalse(Long id);
    Page<ExamSchedule> findAllByDeletedFalse(Pageable pageable);
    List<ExamSchedule> findAllByDeletedFalse();
    boolean existsByExamCodeAndDeletedFalse(String examCode);
    boolean existsByIdAndDeletedFalse(Long id);
}
