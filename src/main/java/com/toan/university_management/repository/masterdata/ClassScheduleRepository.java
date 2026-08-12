package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.ClassSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    Optional<ClassSchedule> findByIdAndDeletedFalse(Long id);
    Page<ClassSchedule> findAllByDeletedFalse(Pageable pageable);
    List<ClassSchedule> findAllByDeletedFalse();
    boolean existsByScheduleCodeAndDeletedFalse(String scheduleCode);
    boolean existsByIdAndDeletedFalse(Long id);
}
