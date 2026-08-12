package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.EnrollmentRequest;
import com.toan.university_management.dto.response.ApiResponse;
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
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<EnrollmentResponse> createEnrollment(@Valid @RequestBody EnrollmentRequest request) {
        return ApiResponse.<EnrollmentResponse>builder()
                .message("Successfully created enrollment")
                .result(enrollmentService.createEnrollment(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<EnrollmentResponse>> getAllEnrollments(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<EnrollmentResponse>>builder()
                .result(enrollmentService.getAllEnrollments(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<EnrollmentResponse>> getAllList() {
        return ApiResponse.<List<EnrollmentResponse>>builder()
                .result(enrollmentService.getAllEnrollments())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<EnrollmentResponse> getEnrollmentById(@PathVariable Long id) {
        return ApiResponse.<EnrollmentResponse>builder()
                .result(enrollmentService.getEnrollmentById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<EnrollmentResponse> updateEnrollment(@PathVariable Long id, @Valid @RequestBody EnrollmentRequest request) {
        return ApiResponse.<EnrollmentResponse>builder()
                .result(enrollmentService.updateEnrollment(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return ApiResponse.<String>builder().result("Enrollment has been deleted successfully").build();
    }
}
