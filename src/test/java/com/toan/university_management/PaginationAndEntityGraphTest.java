package com.toan.university_management;

import com.toan.university_management.dto.response.CourseResponse;
import com.toan.university_management.dto.response.StudentResponse;
import com.toan.university_management.dto.response.TeacherResponse;
import com.toan.university_management.dto.response.UserResponse;
import com.toan.university_management.entity.Course;
import com.toan.university_management.entity.Student;
import com.toan.university_management.entity.Teacher;
import com.toan.university_management.entity.User;
import com.toan.university_management.repository.CourseRepository;
import com.toan.university_management.repository.StudentRepository;
import com.toan.university_management.repository.TeacherRepository;
import com.toan.university_management.repository.UserRepository;
import com.toan.university_management.service.CourseService;
import com.toan.university_management.service.StudentService;
import com.toan.university_management.service.TeacherService;
import com.toan.university_management.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PaginationAndEntityGraphTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        teacherRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Test Student Pagination and EntityGraph")
    void testStudentPagination() {
        // Seed 3 students
        for (int i = 1; i <= 3; i++) {
            studentRepository.save(Student.builder()
                    .studentCode("SV00" + i)
                    .fullName("Student " + i)
                    .email("student" + i + "@gmail.com")
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by("studentCode").ascending());
        Page<StudentResponse> page = studentService.getAllStudents(pageable);

        assertNotNull(page);
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        assertEquals("SV001", page.getContent().get(0).getStudentCode());
        assertEquals("SV002", page.getContent().get(1).getStudentCode());
    }

    @Test
    @DisplayName("Test Teacher Pagination and EntityGraph")
    void testTeacherPagination() {
        // Seed 3 teachers
        for (int i = 1; i <= 3; i++) {
            teacherRepository.save(Teacher.builder()
                    .teacherCode("GV00" + i)
                    .fullName("Teacher " + i)
                    .email("teacher" + i + "@gmail.com")
                    .specialization("CNTT")
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by("teacherCode").ascending());
        Page<TeacherResponse> page = teacherService.getAllTeachers(pageable);

        assertNotNull(page);
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        assertEquals("GV001", page.getContent().get(0).getTeacherCode());
    }

    @Test
    @DisplayName("Test Course Pagination and EntityGraph")
    void testCoursePagination() {
        // Seed 3 courses
        for (int i = 1; i <= 3; i++) {
            courseRepository.save(Course.builder()
                    .courseCode("CS10" + i)
                    .courseName("Course " + i)
                    .credit(3)
                    .semester("2026.1")
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by("courseCode").ascending());
        Page<CourseResponse> page = courseService.getAllCourses(pageable);

        assertNotNull(page);
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        assertEquals("CS101", page.getContent().get(0).getCourseCode());
    }

    @Test
    @DisplayName("Test User Pagination and EntityGraph")
    void testUserPagination() {
        // Seed 3 users
        for (int i = 1; i <= 3; i++) {
            userRepository.save(User.builder()
                    .username("user_test_" + i)
                    .password("password" + i)
                    .email("user" + i + "@gmail.com")
                    .fullName("User " + i)
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by("username").ascending());
        Page<UserResponse> page = userService.getAllUsers(pageable);

        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 3);
        assertEquals(2, page.getContent().size());
    }
}
