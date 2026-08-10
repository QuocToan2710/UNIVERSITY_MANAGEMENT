package com.toan.university_management.repository.masterdata;

import com.toan.university_management.dto.reports.StudentReportDTO;
import com.toan.university_management.entity.masterdata.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    boolean existsByStudentCode(String studentCode);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.courses WHERE s.id = :id")
    Optional<Student> findByIdWithCourses(@Param("id") String id);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.courses")
    List<Student> findAllWithCourses();

    @EntityGraph(attributePaths = {"courses"})
    Page<Student> findAll(Pageable pageable);

    @Query("""
    SELECT new com.toan.university_management.dto.reports.StudentReportDTO(
        s.id,
        s.studentCode,
        s.fullName,
        s.dob,
        s.gender,
        s.phoneNumber,
        s.email,
        s.address
    )
    FROM Student s
""")
    List<StudentReportDTO> getAllStudentForReport();
}


