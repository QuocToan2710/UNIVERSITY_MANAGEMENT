package com.toan.university_management;

import com.toan.university_management.dto.response.identity.UserResponse;
import com.toan.university_management.dto.response.masterdata.StudentResponse;
import com.toan.university_management.dto.response.masterdata.SubjectResponse;
import com.toan.university_management.dto.response.masterdata.TeacherResponse;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.SubjectRepository;
import com.toan.university_management.repository.masterdata.TeacherRepository;
import com.toan.university_management.service.identity.UserService;
import com.toan.university_management.service.masterdata.student.StudentService;
import com.toan.university_management.service.masterdata.subject.SubjectService;
import com.toan.university_management.service.masterdata.teacher.TeacherService;
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
    private SubjectService subjectService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAllInBatch();
        subjectRepository.deleteAllInBatch();
        teacherRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Test Student Pagination")
    void testStudentPagination() {
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
    @DisplayName("Test Teacher Pagination")
    void testTeacherPagination() {
        for (int i = 1; i <= 3; i++) {
            teacherRepository.save(Teacher.builder()
                    .teacherCode("GV00" + i)
                    .fullName("Teacher " + i)
                    .email("teacher" + i + "@gmail.com")
                    .degree("Thạc sĩ")
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
    @DisplayName("Test Subject Pagination")
    void testSubjectPagination() {
        for (int i = 1; i <= 3; i++) {
            subjectRepository.save(Subject.builder()
                    .subjectCode("CS10" + i)
                    .name("Subject " + i)
                    .credit(3)
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 2, Sort.by("subjectCode").ascending());
        Page<SubjectResponse> page = subjectService.getAllSubjects(pageable);

        assertNotNull(page);
        assertEquals(3, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        assertEquals("CS101", page.getContent().get(0).getSubjectCode());
    }

    @Test
    @DisplayName("Test User Pagination")
    void testUserPagination() {
        for (int i = 1; i <= 3; i++) {
            userRepository.save(User.builder()
                    .userCode("USR_TEST_" + i)
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
