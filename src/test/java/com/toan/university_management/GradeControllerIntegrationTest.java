package com.toan.university_management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toan.university_management.dto.request.masterdata.GradeBatchUpdateRequest;
import com.toan.university_management.dto.request.masterdata.GradeItemRequest;
import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.entity.masterdata.SubjectClass;
import com.toan.university_management.enums.EnrollmentStatus;
import com.toan.university_management.enums.GradeStatus;
import com.toan.university_management.repository.masterdata.EnrollmentRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.SubjectClassRepository;
import com.toan.university_management.repository.masterdata.SubjectRepository;
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
public class GradeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectClassRepository subjectClassRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private Student student;
    private Subject subject;
    private SubjectClass subjectClass;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        student = studentRepository.save(Student.builder()
                .studentCode("SV_INTEGRATION_01")
                .fullName("Trần Thị Minh")
                .email("minh.tt@test.edu.vn")
                .build());

        subject = subjectRepository.save(Subject.builder()
                .subjectCode("SUB_WEB_01")
                .name("Phát triển Ứng dụng Web")
                .credit(3)
                .attendanceCoeff(1)
                .midtermCoeff(3)
                .finalCoeff(6)
                .build());

        subjectClass = subjectClassRepository.save(SubjectClass.builder()
                .subjectClassCode("SC_WEB_01")
                .name("Lớp Web 01")
                .subjectId(subject.getId())
                .semester("Học kỳ 1")
                .academicYear("2025-2026")
                .maxCapacity(50)
                .build());

        enrollment = enrollmentRepository.save(Enrollment.builder()
                .enrollmentCode("ENR_INT_01")
                .studentId(student.getId())
                .subjectClassId(subjectClass.getId())
                .status(EnrollmentStatus.REGISTERED)
                .gradeStatus(GradeStatus.DRAFT)
                .build());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /grades/subject-classes/{id} should return grade summary and coefficients")
    void testGetSubjectClassGrades() throws Exception {
        mockMvc.perform(get("/grades/subject-classes/" + subjectClass.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.subjectClassCode", is("SC_WEB_01")))
                .andExpect(jsonPath("$.result.attendanceCoeff", is(1)))
                .andExpect(jsonPath("$.result.midtermCoeff", is(3)))
                .andExpect(jsonPath("$.result.finalCoeff", is(6)))
                .andExpect(jsonPath("$.result.totalStudents", is(1)))
                .andExpect(jsonPath("$.result.studentGrades", hasSize(1)))
                .andExpect(jsonPath("$.result.studentGrades[0].studentCode", is("SV_INTEGRATION_01")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("PUT /grades/subject-classes/{id}/batch should calculate total, letter grade and 4.0 scale")
    void testUpdateBatchGrades() throws Exception {
        GradeBatchUpdateRequest request = GradeBatchUpdateRequest.builder()
                .items(List.of(
                        GradeItemRequest.builder()
                                .enrollmentId(enrollment.getId())
                                .attendanceScore(10.0) // 10 * 1 = 10
                                .midtermScore(8.0)     // 8 * 3 = 24
                                .finalScore(9.0)       // 9 * 6 = 54 -> Total = 88/10 = 8.8 -> A (4.0)
                                .note("Điểm xuất sắc")
                                .build()
                ))
                .build();

        mockMvc.perform(put("/grades/subject-classes/" + subjectClass.getId() + "/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("Cập nhật bảng điểm thành công")))
                .andExpect(jsonPath("$.result.gradedStudents", is(1)))
                .andExpect(jsonPath("$.result.averageScore", is(8.8)))
                .andExpect(jsonPath("$.result.studentGrades[0].totalScore", is(8.8)))
                .andExpect(jsonPath("$.result.studentGrades[0].letterGrade", is("A")))
                .andExpect(jsonPath("$.result.studentGrades[0].gradePoint4", is(4.0)))
                .andExpect(jsonPath("$.result.studentGrades[0].status", is("PASSED")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Grade workflow endpoints: Submit -> Publish -> Lock -> Guard against updates")
    void testWorkflowAndLockGuard() throws Exception {
        // 1. Submit
        mockMvc.perform(post("/grades/subject-classes/" + subjectClass.getId() + "/submit")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.gradeStatus", is("SUBMITTED")));

        // 2. Publish
        mockMvc.perform(post("/grades/subject-classes/" + subjectClass.getId() + "/publish")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.gradeStatus", is("PUBLISHED")));

        // 3. Lock
        mockMvc.perform(post("/grades/subject-classes/" + subjectClass.getId() + "/lock")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.gradeStatus", is("LOCKED")));

        // 4. Updating a locked class must fail
        GradeBatchUpdateRequest request = GradeBatchUpdateRequest.builder()
                .items(List.of(
                        GradeItemRequest.builder()
                                .enrollmentId(enrollment.getId())
                                .attendanceScore(8.0)
                                .build()
                ))
                .build();

        mockMvc.perform(put("/grades/subject-classes/" + subjectClass.getId() + "/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(2020))); // GRADE_LOCKED
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("GET /grades/student/{studentId}/transcript should return full transcript with GPA/CPA")
    void testGetStudentTranscript() throws Exception {
        // Populate grade for enrollment
        enrollment.setAttendanceScore(9.0);
        enrollment.setMidtermScore(8.0);
        enrollment.setFinalScore(9.0);
        // (9*1 + 8*3 + 9*6)/10 = 8.7 -> A (4.0)
        com.toan.university_management.util.GradeCalculator.computeAndApplyGrades(enrollment, subject);
        enrollmentRepository.save(enrollment);

        mockMvc.perform(get("/grades/student/" + student.getId() + "/transcript")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.studentCode", is("SV_INTEGRATION_01")))
                .andExpect(jsonPath("$.result.totalRegisteredCredits", is(3)))
                .andExpect(jsonPath("$.result.totalEarnedCredits", is(3)))
                .andExpect(jsonPath("$.result.cumulativeCpa4", is(4.0)))
                .andExpect(jsonPath("$.result.cumulativeGpa10", is(8.7)))
                .andExpect(jsonPath("$.result.academicRank", is("Xuất sắc")))
                .andExpect(jsonPath("$.result.semesters", hasSize(1)))
                .andExpect(jsonPath("$.result.semesters[0].semesterGpa4", is(4.0)));
    }
}