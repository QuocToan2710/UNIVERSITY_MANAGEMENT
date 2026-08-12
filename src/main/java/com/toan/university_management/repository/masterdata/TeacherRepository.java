package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByIdAndDeletedFalse(Long id);
    Page<Teacher> findAllByDeletedFalse(Pageable pageable);
    List<Teacher> findAllByDeletedFalse();
    List<Teacher> findAllByIdInAndDeletedFalse(Collection<Long> ids);
    boolean existsByTeacherCodeAndDeletedFalse(String teacherCode);
    boolean existsByIdAndDeletedFalse(Long id);
}
