package com.toan.university_management;

import com.toan.university_management.entity.Course;
import com.toan.university_management.entity.Student;
import com.toan.university_management.entity.Teacher;
import com.toan.university_management.entity.User;
import com.toan.university_management.repository.CourseRepository;
import com.toan.university_management.repository.StudentRepository;
import com.toan.university_management.repository.TeacherRepository;
import com.toan.university_management.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class SoftDeleteTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Test Soft Delete for Student entity")
    void testStudentSoftDelete() {
        Student student = Student.builder()
                .studentCode("SV001")
                .fullName("Nguyen Van A")
                .email("a@gmail.com")
                .build();
        Student saved = studentRepository.save(student);
        String id = saved.getId();
        assertNotNull(id);
        assertFalse(saved.isDeleted());

        // Perform delete
        studentRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        // Verify entity is soft-deleted (not returned by findById)
        assertTrue(studentRepository.findById(id).isEmpty());
        assertFalse(studentRepository.existsById(id));

        // Verify direct SQL query finds the soft-deleted record with deleted = true
        Object result = entityManager.createNativeQuery("SELECT deleted FROM student WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();
        assertTrue((Boolean) result || Boolean.TRUE.equals(result));
    }

    @Test
    @DisplayName("Test Soft Delete for Teacher entity")
    void testTeacherSoftDelete() {
        Teacher teacher = Teacher.builder()
                .teacherCode("GV001")
                .fullName("Tran Van B")
                .email("b@gmail.com")
                .specialization("CNTT")
                .build();
        Teacher saved = teacherRepository.save(teacher);
        String id = saved.getId();

        teacherRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        assertTrue(teacherRepository.findById(id).isEmpty());
        Object result = entityManager.createNativeQuery("SELECT deleted FROM teacher WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();
        assertTrue((Boolean) result || Boolean.TRUE.equals(result));
    }

    @Test
    @DisplayName("Test Soft Delete for Course entity")
    void testCourseSoftDelete() {
        Course course = Course.builder()
                .courseCode("CS101")
                .courseName("Lap trinh Java")
                .credit(3)
                .semester("2026.1")
                .build();
        Course saved = courseRepository.save(course);
        String id = saved.getId();

        courseRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        assertTrue(courseRepository.findById(id).isEmpty());
        Object result = entityManager.createNativeQuery("SELECT deleted FROM course WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();
        assertTrue((Boolean) result || Boolean.TRUE.equals(result));
    }

    @Test
    @DisplayName("Test Soft Delete for User entity")
    void testUserSoftDelete() {
        User user = User.builder()
                .username("testuser")
                .password("password123")
                .email("user@gmail.com")
                .fullName("User Test")
                .build();
        User saved = userRepository.save(user);
        String id = saved.getId();

        userRepository.deleteById(id);
        entityManager.flush();
        entityManager.clear();

        assertTrue(userRepository.findById(id).isEmpty());
        Object result = entityManager.createNativeQuery("SELECT deleted FROM user WHERE id = :id")
                .setParameter("id", id)
                .getSingleResult();
        assertTrue((Boolean) result || Boolean.TRUE.equals(result));
    }
}
