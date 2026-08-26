package com.toan.university_management;

import com.toan.university_management.dto.request.masterdata.BatchEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.ClassGroupEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.EnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.StudentRegistrationRequest;
import com.toan.university_management.dto.response.masterdata.AvailableSubjectClassResponse;
import com.toan.university_management.dto.response.masterdata.BatchEnrollmentResultResponse;
import com.toan.university_management.dto.response.masterdata.EnrollmentResponse;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.masterdata.*;
import com.toan.university_management.enums.WeekDay;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.masterdata.*;
import com.toan.university_management.service.masterdata.enrollment.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class EnrollmentRegistrationServiceTest {

    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SubjectClassRepository subjectClassRepository;

    @Autowired
    ClassGroupRepository classGroupRepository;

    @Autowired
    ClassScheduleRepository classScheduleRepository;

    private User testUser;
    private Student student1;
    private Student student2;
    private ClassGroup testClassGroup;
    private Subject testSubject;
    private SubjectClass testSubjectClass;
    private SubjectClass testSubjectClassConflict;
    private String uniqueSuffix;

    @BeforeEach
    void setUp() {
        uniqueSuffix = String.valueOf(System.nanoTime());

        testUser = userRepository.save(User.builder()
                .username("sv_test_reg_" + uniqueSuffix)
                .email("sv_test_reg_" + uniqueSuffix + "@student.edu.vn")
                .fullName("Sinh Viên Test ĐK " + uniqueSuffix)
                .password("plain123")
                .build());

        testClassGroup = classGroupRepository.save(ClassGroup.builder()
                .classCode("CNTT_" + uniqueSuffix)
                .className("Công nghệ thông tin " + uniqueSuffix)
                .academicYear("2025-2026")
                .build());

        student1 = studentRepository.save(Student.builder()
                .studentCode("SV_A_" + uniqueSuffix)
                .fullName("Nguyễn Văn A " + uniqueSuffix)
                .email("sv_test_reg_" + uniqueSuffix + "@student.edu.vn")
                .userId(testUser.getId())
                .classGroupId(testClassGroup.getId())
                .build());

        student2 = studentRepository.save(Student.builder()
                .studentCode("SV_B_" + uniqueSuffix)
                .fullName("Trần Thị B " + uniqueSuffix)
                .email("sv2_" + uniqueSuffix + "@student.edu.vn")
                .classGroupId(testClassGroup.getId())
                .build());

        testSubject = subjectRepository.save(Subject.builder()
                .subjectCode("SUB_" + uniqueSuffix)
                .name("Môn học Test " + uniqueSuffix)
                .credit(3)
                .attendanceCoeff(1)
                .midtermCoeff(3)
                .finalCoeff(6)
                .build());

        testSubjectClass = subjectClassRepository.save(SubjectClass.builder()
                .subjectClassCode("CLS_01_" + uniqueSuffix)
                .name("Lớp HP 01 " + uniqueSuffix)
                .subjectId(testSubject.getId())
                .semester("HK1")
                .academicYear("2025-2026")
                .maxCapacity(2)
                .build());

        testSubjectClassConflict = subjectClassRepository.save(SubjectClass.builder()
                .subjectClassCode("CLS_02_" + uniqueSuffix)
                .name("Lớp HP 02 " + uniqueSuffix)
                .subjectId(testSubject.getId())
                .semester("HK1")
                .academicYear("2025-2026")
                .maxCapacity(50)
                .build());

        // Lịch học lớp 1: Thứ 2, 07:00 - 09:30
        classScheduleRepository.save(ClassSchedule.builder()
                .scheduleCode("SCH_01_" + uniqueSuffix)
                .subjectClassId(testSubjectClass.getId())
                .dayOfWeek(WeekDay.MONDAY)
                .startTime(LocalTime.of(7, 0))
                .endTime(LocalTime.of(9, 30))
                .room("A101")
                .semester("HK1")
                .academicYear("2025-2026")
                .build());

        // Lịch học lớp 2 (Conflict): Thứ 2, 08:00 - 10:30 (Trùng từ 08:00 đến 09:30)
        classScheduleRepository.save(ClassSchedule.builder()
                .scheduleCode("SCH_02_" + uniqueSuffix)
                .subjectClassId(testSubjectClassConflict.getId())
                .dayOfWeek(WeekDay.MONDAY)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 30))
                .room("B202")
                .semester("HK1")
                .academicYear("2025-2026")
                .build());

        // Set security context for student1
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        testUser.getUsername(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
                )
        );
    }

    @Test
    @DisplayName("1. Đăng ký học phần thành công & kiểm tra thông tin trả về")
    void testCreateEnrollmentSuccess() {
        EnrollmentRequest request = EnrollmentRequest.builder()
                .studentId(student1.getId())
                .subjectClassId(testSubjectClass.getId())
                .build();

        EnrollmentResponse response = enrollmentService.createEnrollment(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(student1.getStudentCode(), response.getStudentCode());
        assertEquals(testSubjectClass.getSubjectClassCode(), response.getSubjectClassCode());
        assertEquals(3, response.getCredit());
        assertTrue(enrollmentRepository.existsByStudentIdAndSubjectClassIdAndDeletedFalse(student1.getId(), testSubjectClass.getId()));
    }

    @Test
    @DisplayName("2. Sinh viên tự đăng ký qua registerStudent và kiểm tra my-registrations")
    void testStudentSelfRegistration() {
        StudentRegistrationRequest regReq = StudentRegistrationRequest.builder()
                .subjectClassId(testSubjectClass.getId())
                .build();

        EnrollmentResponse response = enrollmentService.registerStudent(regReq);
        assertNotNull(response);
        assertEquals(student1.getId(), response.getStudentId());

        List<EnrollmentResponse> myRegs = enrollmentService.getMyRegistrations("HK1", "2025-2026");
        assertFalse(myRegs.isEmpty());
        assertTrue(myRegs.stream().anyMatch(r -> r.getSubjectClassCode().equals(testSubjectClass.getSubjectClassCode())));
    }

    @Test
    @DisplayName("3. Đăng ký lớp đã đầy sĩ số -> Ném AppException ENROLLMENT_CAPACITY_FULL")
    void testCapacityFullException() {
        // Đăng ký 2 bạn cho lớp maxCapacity = 2
        enrollmentService.createEnrollment(EnrollmentRequest.builder().studentId(student1.getId()).subjectClassId(testSubjectClass.getId()).build());
        enrollmentService.createEnrollment(EnrollmentRequest.builder().studentId(student2.getId()).subjectClassId(testSubjectClass.getId()).build());

        // Tạo sinh viên thứ 3
        Student student3 = studentRepository.save(Student.builder()
                .studentCode("SV_C_" + uniqueSuffix)
                .fullName("Lê Văn C " + uniqueSuffix)
                .build());

        AppException ex = assertThrows(AppException.class, () -> {
            enrollmentService.createEnrollment(EnrollmentRequest.builder().studentId(student3.getId()).subjectClassId(testSubjectClass.getId()).build());
        });

        assertEquals(ErrorCode.ENROLLMENT_CAPACITY_FULL, ex.getErrorCode());
    }

    @Test
    @DisplayName("4. Đăng ký trùng lớp học phần -> Ném AppException ENROLLMENT_ALREADY_EXISTS")
    void testAlreadyEnrolledException() {
        enrollmentService.createEnrollment(EnrollmentRequest.builder().studentId(student1.getId()).subjectClassId(testSubjectClass.getId()).build());

        AppException ex = assertThrows(AppException.class, () -> {
            enrollmentService.createEnrollment(EnrollmentRequest.builder().studentId(student1.getId()).subjectClassId(testSubjectClass.getId()).build());
        });

        assertEquals(ErrorCode.ENROLLMENT_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    @DisplayName("5. Đăng ký 2 lớp bị trùng giờ học -> Ném AppException ENROLLMENT_SCHEDULE_CONFLICT")
    void testScheduleConflictException() {
        // Đăng ký lớp 1 (Thứ 2, 07:00 - 09:30)
        enrollmentService.createEnrollment(EnrollmentRequest.builder().studentId(student1.getId()).subjectClassId(testSubjectClass.getId()).build());

        // Đăng ký lớp 2 (Thứ 2, 08:00 - 10:30) -> Trùng giờ!
        AppException ex = assertThrows(AppException.class, () -> {
            enrollmentService.createEnrollment(EnrollmentRequest.builder().studentId(student1.getId()).subjectClassId(testSubjectClassConflict.getId()).build());
        });

        assertEquals(ErrorCode.ENROLLMENT_SCHEDULE_CONFLICT, ex.getErrorCode());
    }

    @Test
    @DisplayName("6. Hủy đăng ký học phần -> Bản ghi chuyển deleted = true")
    void testCancelRegistration() {
        enrollmentService.createEnrollment(EnrollmentRequest.builder().studentId(student1.getId()).subjectClassId(testSubjectClass.getId()).build());
        assertTrue(enrollmentRepository.existsByStudentIdAndSubjectClassIdAndDeletedFalse(student1.getId(), testSubjectClass.getId()));

        enrollmentService.cancelRegistration(testSubjectClass.getId());

        assertFalse(enrollmentRepository.existsByStudentIdAndSubjectClassIdAndDeletedFalse(student1.getId(), testSubjectClass.getId()));
    }

    @Test
    @DisplayName("7. Admin gán cả Lớp sinh hoạt vào Lớp học phần (enrollClassGroup)")
    void testEnrollClassGroup() {
        ClassGroupEnrollmentRequest req = ClassGroupEnrollmentRequest.builder()
                .classGroupId(testClassGroup.getId())
                .subjectClassId(testSubjectClassConflict.getId()) // maxCapacity = 50
                .build();

        BatchEnrollmentResultResponse result = enrollmentService.enrollClassGroup(req);

        assertEquals(2, result.getTotalRequested());
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailedCount());
        assertEquals(2, enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(testSubjectClassConflict.getId()).size());
    }

    @Test
    @DisplayName("8. Tra cứu danh sách môn mở đăng ký (getAvailableClassesForRegistration)")
    void testGetAvailableClassesForRegistration() {
        // student1 đăng ký lớp 1
        enrollmentService.createEnrollment(EnrollmentRequest.builder().studentId(student1.getId()).subjectClassId(testSubjectClass.getId()).build());

        List<AvailableSubjectClassResponse> availList = enrollmentService.getAvailableClassesForRegistration("HK1", "2025-2026");

        assertFalse(availList.isEmpty());
        AvailableSubjectClassResponse sc1 = availList.stream()
                .filter(c -> c.getId().equals(testSubjectClass.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(sc1);
        assertTrue(sc1.isEnrolled()); // Student 1 đã đăng ký
        assertEquals(1, sc1.getCurrentCapacity());
        assertEquals(2, sc1.getMaxCapacity());
        assertFalse(sc1.getSchedules().isEmpty());
    }
}