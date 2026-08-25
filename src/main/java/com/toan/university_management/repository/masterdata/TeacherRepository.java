package com.toan.university_management.repository.masterdata;

import com.toan.university_management.common.repository.BaseRepository;
import com.toan.university_management.entity.masterdata.Teacher;

import java.util.Optional;

public interface TeacherRepository extends BaseRepository<Teacher, Long> {
    Optional<Teacher> findByUserIdAndDeletedFalse(Long userId);
    Optional<Teacher> findByTeacherCodeAndDeletedFalse(String teacherCode);
    Optional<Teacher> findByEmailAndDeletedFalse(String email);
    boolean existsByTeacherCodeAndDeletedFalse(String teacherCode);
}
