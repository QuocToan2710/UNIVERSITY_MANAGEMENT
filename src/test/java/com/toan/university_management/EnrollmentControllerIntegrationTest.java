package com.toan.university_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toan.university_management.dto.request.masterdata.BatchEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.ClassGroupEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.StudentRegistrationRequest;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.masterdata.*;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.masterdata.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class EnrollmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectClassRepository subjectClassRepository;

    @Autowired
    private ClassGroupRepository classGroupRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private User studentUser;
    private Student student;
    private Subject subject;
    private SubjectClass subjectClass;
    private ClassGroup classGroup;
    private String suffix;

    @BeforeEach
    void setUp() {
        suffix = String.valueOf(System.nanoTime());

        studentUser = userRepository.save(User.builder()
                .username("student_mock_" + suffix)
                .email("student_mock_" + suffix + "@test.edu.vn")
                .fullName("Student Mock " + suffix)
                .password("plain123")
                .build());

        classGroup = classGroupRepository.save(ClassGroup.builder()
                .classCode("CG_MOCK_" + suffix)
                .className("Class Group Mock " + suffix)
                .build());

        student = studentRepository.save(Student.builder()
                .studentCode("SVMOCK_" + suffix)
                .fullName("Student Mock " + suffix)
                .userId(studentUser.getId())
                .classGroupId(classGroup.getId())
                .email("student_mock_" + suffix + "@test.edu.vn")
                .build());

        subject = subjectRepository.save(Subject.builder()
                .subjectCode("SUB_MOCK_" + suffix)
                .name("Subject Mock " + suffix)
                .credit(4)
                .build());

        subjectClass = subjectClassRepository.save(SubjectClass.builder()
                .subjectClassCode("SC_MOCK_" + suffix)
                .name("Subject Class Mock " + suffix)
                .subjectId(subject.getId())
                .semester("HK1")
                .academicYear("2025-2026")
                .maxCapacity(50)
                .build());
    }

    @Test
    @DisplayName("API: POST /enrollments/register - Sinh viên tự đăng ký học phần")
    void testApiStudentRegisterCourse() throws Exception {
        StudentRegistrationRequest req = StudentRegistrationRequest.builder()
                .subjectClassId(subjectClass.getId())
                .build();

        mockMvc.perform(post("/enrollments/register")
                        .header("X-Mock-User", studentUser.getUsername())
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(studentUser.getUsername()).roles("STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.studentCode", is(student.getStudentCode())))
                .andExpect(jsonPath("$.result.subjectClassCode", is(subjectClass.getSubjectClassCode())));
    }

    @Test
    @DisplayName("API: GET /enrollments/available-classes - Lấy danh sách lớp học phần đang mở")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testApiGetAvailableClasses() throws Exception {
        mockMvc.perform(get("/enrollments/available-classes")
                        .param("semester", "HK1")
                        .param("academicYear", "2025-2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", isA(List.class)))
                .andExpect(jsonPath("$.result", not(empty())));
    }

    @Test
    @DisplayName("API: POST /enrollments/batch - Admin gán hàng loạt sinh viên")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testApiBatchEnroll() throws Exception {
        BatchEnrollmentRequest req = BatchEnrollmentRequest.builder()
                .subjectClassId(subjectClass.getId())
                .studentIds(List.of(student.getId()))
                .build();

        mockMvc.perform(post("/enrollments/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalRequested", is(1)))
                .andExpect(jsonPath("$.result.successCount", is(1)));
    }

    @Test
    @DisplayName("API: POST /enrollments/class-group - Admin gán cả lớp sinh hoạt")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testApiClassGroupEnroll() throws Exception {
        ClassGroupEnrollmentRequest req = ClassGroupEnrollmentRequest.builder()
                .subjectClassId(subjectClass.getId())
                .classGroupId(classGroup.getId())
                .build();

        mockMvc.perform(post("/enrollments/class-group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.totalRequested", is(1)))
                .andExpect(jsonPath("$.result.successCount", is(1)));
    }

    @Test
    @DisplayName("API: DELETE /enrollments/cancel/{subjectClassId} - Sinh viên hủy đăng ký học phần")
    void testApiCancelRegistration() throws Exception {
        // Ghi danh trước
        enrollmentRepository.save(Enrollment.builder()
                .enrollmentCode("ENR_" + suffix)
                .studentId(student.getId())
                .subjectClassId(subjectClass.getId())
                .build());

        mockMvc.perform(delete("/enrollments/cancel/" + subjectClass.getId())
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(studentUser.getUsername()).roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", containsString("thành công")));
    }
}