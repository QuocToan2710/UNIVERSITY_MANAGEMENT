package com.toan.university_management.repository.masterdata;

import com.toan.university_management.entity.masterdata.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, String> {
    boolean existsByTeacherCode(String teacherCode);

    @EntityGraph(attributePaths = {"courses"})
    Page<Teacher> findAll(Pageable pageable);

    @Query("""
        SELECT t FROM Teacher t
        WHERE LOWER(t.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))
    """)
    List<Teacher> findBySpecializationContainingIgnoreCase(@Param("specialization") String specialization);
}


