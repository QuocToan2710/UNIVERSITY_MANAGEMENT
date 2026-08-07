package com.toan.university_management.repository;

import com.toan.university_management.dto.response.CourseTeacherProjection;
import com.toan.university_management.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> /*CourseRepositoryCustom*/ {
    @Query("""
        SELECT c.id AS id,
               c.courseCode AS courseCode,
               c.courseName AS courseName,
               c.credit AS credit,
               c.semester AS semester,
               t.id AS teacherId,
               t.teacherCode AS teacherCode,
               t.fullName AS teacherName,
               t.email AS teacherEmail,
               t.phoneNumber AS teacherPhone,
               t.specialization AS teacherSpecialization
        FROM Course c
        JOIN c.teacher t
        WHERE LOWER(t.fullName) LIKE LOWER(CONCAT('%', :teacherName, '%'))
    """)
    List<CourseTeacherProjection> findCourseWithTeacherInfoByTeacherName(@Param("teacherName") String teacherName);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.teacher t WHERE LOWER(t.fullName) LIKE LOWER(CONCAT('%', :teacherName, '%'))")
    List<Course> findByTeacherNameWithTeacherInfo(@Param("teacherName") String teacherName);
}
