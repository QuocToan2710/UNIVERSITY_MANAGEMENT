package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.BatchEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.ClassGroupEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.EnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.StudentRegistrationRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.AvailableSubjectClassResponse;
import com.toan.university_management.dto.response.masterdata.BatchEnrollmentResultResponse;
import com.toan.university_management.dto.response.masterdata.EnrollmentResponse;
import com.toan.university_management.service.masterdata.enrollment.EnrollmentService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnrollmentController {
    EnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<EnrollmentResponse> createEnrollment(@Valid @RequestBody EnrollmentRequest request) {
        return ApiResponse.<EnrollmentResponse>builder()
                .message("Tạo bản ghi đăng ký học phần thành công")
                .result(enrollmentService.createEnrollment(request))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<EnrollmentResponse>> getAllEnrollments(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<EnrollmentResponse>>builder()
                .result(enrollmentService.getAllEnrollments(pageable))
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<EnrollmentResponse>> getAllList() {
        return ApiResponse.<List<EnrollmentResponse>>builder()
                .result(enrollmentService.getAllEnrollments())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        return ApiResponse.<EnrollmentResponse>builder()
                .result(enrollmentService.getEnrollmentById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<EnrollmentResponse> updateEnrollment(@PathVariable Long id, @Valid @RequestBody EnrollmentRequest request) {
        return ApiResponse.<EnrollmentResponse>builder()
                .message("Cập nhật đăng ký học phần thành công")
                .result(enrollmentService.updateEnrollment(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<String> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ApiResponse.<String>builder().result("Xóa đăng ký học phần thành công").build();
    }

    // --- STUDENT SELF-SERVICE COURSE REGISTRATION ---

    @PostMapping("/register")
    public ApiResponse<EnrollmentResponse> registerStudent(@Valid @RequestBody StudentRegistrationRequest request) {
        return ApiResponse.<EnrollmentResponse>builder()
                .message("Đăng ký học phần thành công")
                .result(enrollmentService.registerStudent(request))
                .build();
    }

    @DeleteMapping("/cancel/{subjectClassId}")
    public ApiResponse<String> cancelRegistration(@PathVariable Long subjectClassId) {
        enrollmentService.cancelRegistration(subjectClassId);
        return ApiResponse.<String>builder()
                .message("Hủy đăng ký học phần thành công")
                .result("Đã hủy đăng ký học phần thành công")
                .build();
    }

    @DeleteMapping("/my-registrations/{enrollmentId}")
    public ApiResponse<String> cancelRegistrationById(@PathVariable Long enrollmentId) {
        enrollmentService.cancelRegistrationById(enrollmentId);
        return ApiResponse.<String>builder()
                .message("Hủy đăng ký học phần thành công")
                .result("Đã hủy đăng ký học phần thành công")
                .build();
    }

    @GetMapping("/my-registrations")
    public ApiResponse<List<EnrollmentResponse>> getMyRegistrations(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        return ApiResponse.<List<EnrollmentResponse>>builder()
                .result(enrollmentService.getMyRegistrations(semester, academicYear))
                .build();
    }

    @GetMapping("/available-classes")
    public ApiResponse<List<AvailableSubjectClassResponse>> getAvailableClasses(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear) {
        return ApiResponse.<List<AvailableSubjectClassResponse>>builder()
                .result(enrollmentService.getAvailableClassesForRegistration(semester, academicYear))
                .build();
    }

    // --- ADMIN & TEACHER MANAGEMENT APIS ---

    @GetMapping("/subject-class/{subjectClassId}")
    public ApiResponse<List<EnrollmentResponse>> getEnrollmentsBySubjectClass(@PathVariable Long subjectClassId) {
        return ApiResponse.<List<EnrollmentResponse>>builder()
                .result(enrollmentService.getEnrollmentsBySubjectClass(subjectClassId))
                .build();
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<BatchEnrollmentResultResponse> batchEnroll(@Valid @RequestBody BatchEnrollmentRequest request) {
        return ApiResponse.<BatchEnrollmentResultResponse>builder()
                .message("Đã hoàn tất xử lý thêm sinh viên vào lớp học phần")
                .result(enrollmentService.batchEnroll(request))
                .build();
    }

    @PostMapping("/class-group")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ApiResponse<BatchEnrollmentResultResponse> enrollClassGroup(@Valid @RequestBody ClassGroupEnrollmentRequest request) {
        return ApiResponse.<BatchEnrollmentResultResponse>builder()
                .message("Đã hoàn tất gán lớp sinh hoạt vào lớp học phần")
                .result(enrollmentService.enrollClassGroup(request))
                .build();
    }
}
