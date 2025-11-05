//package com.toan.university_management.repository;
//
//import com.toan.university_management.dto.response.CourseTeacherProjection;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Repository
//public class CourseRepositoryImpl implements CourseRepositoryCustom{
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Override
//    public List<CourseTeacherProjection> findCourseWithTeacherInfoByTeacherName(String teacherName) {
//        String jpql = """
//            SELECT c.id AS id,
//                   c.courseCode AS courseCode,
//                   c.courseName AS courseName,
//                   c.credit AS credit,
//                   c.semester AS semester,
//                   t.id AS teacherId,
//                   t.teacherCode AS teacherCode,
//                   t.fullName AS teacherName,
//                   t.email AS teacherEmail,
//                   t.phoneNumber AS teacherPhone,
//                   t.specialization AS teacherSpecialization
//            FROM Course c
//            JOIN c.teacher t
//            WHERE LOWER(t.fullName) LIKE LOWER(CONCAT('%', :teacherName, '%'))
//        """;
//        var results = entityManager.createQuery(jpql, Object[].class)
//                .setParameter("teacherName", teacherName)
//                .getResultList();
//
//        return results.stream()
//                .map(row -> new CourseTeacherProjection() {
//                    @Override public String getId() { return row[0].toString(); }
//                    @Override public String getCourseCode() { return (String) row[1]; }
//                    @Override public String getCourseName() { return (String) row[2]; }
//                    @Override public Integer getCredit() { return ((Number) row[3]).intValue(); }
//                    @Override public String getSemester() { return (String) row[4]; }
//                    @Override public String getTeacherId() { return row[5].toString(); }
//                    @Override public String getTeacherCode() { return (String) row[6]; }
//                    @Override public String getTeacherName() { return (String) row[7]; }
//                    @Override public String getTeacherEmail() { return (String) row[8]; }
//                    @Override public String getTeacherPhone() { return (String) row[9]; }
//                    @Override public String getTeacherSpecialization() { return (String) row[10]; }
//                })
//                .collect(Collectors.toList());
//        }
//}
