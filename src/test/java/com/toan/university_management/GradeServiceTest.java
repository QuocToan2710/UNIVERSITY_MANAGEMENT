package com.toan.university_management;

import com.toan.university_management.dto.request.masterdata.GradeBatchUpdateRequest;
import com.toan.university_management.dto.request.masterdata.GradeItemRequest;
import com.toan.university_management.dto.response.masterdata.StudentTranscriptResponse;
import com.toan.university_management.dto.response.masterdata.SubjectClassGradeSummaryResponse;
import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.entity.masterdata.SubjectClass;
import com.toan.university_management.enums.EnrollmentStatus;
import com.toan.university_management.enums.GradeStatus;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.repository.masterdata.EnrollmentRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.SubjectClassRepository;
import com.toan.university_management.repository.masterdata.SubjectRepository;
import com.toan.university_management.service.masterdata.grade.GradeService;
import com.toan.university_management.util.GradeCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class GradeServiceTest {

    @Autowired
    GradeService gradeService;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SubjectClassRepository subjectClassRepository;

    @Autowired
    StudentRepository studentRepository;

    private Student testStudent;
    private Subject testSubject;
    private SubjectClass testSubjectClass;
    private Enrollment testEnrollment;

    @BeforeEach
    void setUp() {
        testStudent = studentRepository.save(Student.builder()
                .studentCode("SV_TEST_GRADE_01")
                .fullName("Nguyễn Văn Học")
                .email("hoc.nv@test.edu.vn")
                .build());

        testSubject = subjectRepository.save(Subject.builder()
                .subjectCode("SUB_JAVA_01")
                .name("Lập trình Java Nâng cao")
                .credit(3)
                .attendanceCoeff(1)
                .midtermCoeff(3)
                .finalCoeff(6)
                .build());

        testSubjectClass = subjectClassRepository.save(SubjectClass.builder()
                .subjectClassCode("SC_JAVA_01")
                .name("Lớp Java 01")
                .subjectId(testSubject.getId())
                .semester("Học kỳ 1")
                .academicYear("2025-2026")
                .maxCapacity(40)
                .build());

        testEnrollment = enrollmentRepository.save(Enrollment.builder()
                .enrollmentCode("ENR_TEST_01")
                .studentId(testStudent.getId())
                .subjectClassId(testSubjectClass.getId())
                .status(EnrollmentStatus.REGISTERED)
                .gradeStatus(GradeStatus.DRAFT)
                .build());
    }

    @Test
    @DisplayName("Test dynamic coefficients grade calculation (1 - 3 - 6)")
    void testDynamicCoefficientsCalculation() {
        Enrollment enrollment = Enrollment.builder()
                .attendanceScore(9.0)
                .midtermScore(7.0)
                .finalScore(8.0)
                .build();

        GradeCalculator.computeAndApplyGrades(enrollment, testSubject);

        // (9.0*1 + 7.0*3 + 8.0*6) / 10 = (9 + 21 + 48) / 10 = 7.8
        assertEquals(7.8, enrollment.getTotalScore());
        assertEquals("B", enrollment.getLetterGrade());
        assertEquals(3.0, enrollment.getGradePoint4());
        assertEquals(EnrollmentStatus.PASSED, enrollment.getStatus());
    }

    @Test
    @DisplayName("Test attendance failing threshold (< 4.0)")
    void testAttendanceFailingThreshold() {
        Enrollment enrollment = Enrollment.builder()
                .attendanceScore(3.5)
                .midtermScore(8.0)
                .finalScore(8.0)
                .build();

        GradeCalculator.computeAndApplyGrades(enrollment, testSubject);

        assertTrue(enrollment.getNote().contains("Không đủ điều kiện dự thi"));
        assertEquals(EnrollmentStatus.FAILED, enrollment.getStatus());
    }

    @Test
    @DisplayName("Test batch updating grades via GradeService")
    void testBatchUpdateGrades() {
        GradeBatchUpdateRequest request = GradeBatchUpdateRequest.builder()
                .items(List.of(
                        GradeItemRequest.builder()
                                .enrollmentId(testEnrollment.getId())
                                .attendanceScore(9.0)
                                .midtermScore(8.0)
                                .finalScore(8.5)
                                .note("Sinh viên tích cực phát biểu")
                                .build()
                ))
                .build();

        SubjectClassGradeSummaryResponse summary = gradeService.updateBatchGrades(testSubjectClass.getId(), request);

        assertNotNull(summary);
        assertEquals(1, summary.getGradedStudents());
        assertEquals(1, summary.getPassedCount());
        // (9*1 + 8*3 + 8.5*6) / 10 = (9 + 24 + 51) / 10 = 8.4 -> B+
        assertEquals(8.4, summary.getAverageScore());
        assertEquals(1, summary.getGradeDistribution().get("B+"));
    }

    @Test
    @DisplayName("Test grade workflow: DRAFT -> SUBMITTED -> PUBLISHED -> LOCKED")
    void testGradeWorkflowTransitions() {
        // 1. Submit
        SubjectClassGradeSummaryResponse submitted = gradeService.submitGrades(testSubjectClass.getId());
        assertEquals(GradeStatus.SUBMITTED, submitted.getGradeStatus());

        // 2. Publish
        SubjectClassGradeSummaryResponse published = gradeService.publishGrades(testSubjectClass.getId());
        assertEquals(GradeStatus.PUBLISHED, published.getGradeStatus());

        // 3. Lock
        SubjectClassGradeSummaryResponse locked = gradeService.lockGrades(testSubjectClass.getId());
        assertEquals(GradeStatus.LOCKED, locked.getGradeStatus());

        // 4. Updating locked class should throw GRADE_LOCKED
        GradeBatchUpdateRequest request = GradeBatchUpdateRequest.builder()
                .items(List.of(
                        GradeItemRequest.builder()
                                .enrollmentId(testEnrollment.getId())
                                .attendanceScore(10.0)
                                .build()
                ))
                .build();

        AppException ex = assertThrows(AppException.class, () -> 
                gradeService.updateBatchGrades(testSubjectClass.getId(), request));
        assertEquals(ErrorCode.GRADE_LOCKED, ex.getErrorCode());
    }

    @Test
    @DisplayName("Test student transcript and cumulative GPA / CPA calculation")
    void testStudentTranscriptCalculation() {
        // Set grade for first enrollment
        testEnrollment.setAttendanceScore(9.0);
        testEnrollment.setMidtermScore(8.0);
        testEnrollment.setFinalScore(8.5);
        GradeCalculator.computeAndApplyGrades(testEnrollment, testSubject);
        enrollmentRepository.save(testEnrollment);

        StudentTranscriptResponse transcript = gradeService.getStudentTranscript(testStudent.getId());

        assertNotNull(transcript);
        assertEquals(testStudent.getStudentCode(), transcript.getStudentCode());
        assertEquals(3, transcript.getTotalRegisteredCredits());
        assertEquals(3, transcript.getTotalEarnedCredits());
        // B+ = 3.5
        assertEquals(3.5, transcript.getCumulativeCpa4());
        assertEquals(8.4, transcript.getCumulativeGpa10());
        assertEquals("Giỏi", transcript.getAcademicRank());
        assertEquals(1, transcript.getSemesters().size());
    }
}
