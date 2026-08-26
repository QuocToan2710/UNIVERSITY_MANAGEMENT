package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.RecordPaymentRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.StudentTuitionSummaryResponse;
import com.toan.university_management.dto.response.masterdata.TuitionDashboardSummaryResponse;
import com.toan.university_management.enums.TuitionStatus;
import com.toan.university_management.service.masterdata.tuition.TuitionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tuition-fees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TuitionController {

    TuitionService tuitionService;

    @GetMapping("/my-summary")
    public ApiResponse<StudentTuitionSummaryResponse> getMyTuitionSummary(
            @RequestParam(required = false, defaultValue = "1") String semester,
            @RequestParam(required = false, defaultValue = "2024-2025") String academicYear
    ) {
        return ApiResponse.<StudentTuitionSummaryResponse>builder()
                .result(tuitionService.getMyTuitionSummary(semester, academicYear))
                .build();
    }

    @GetMapping("/my-history")
    public ApiResponse<List<StudentTuitionSummaryResponse>> getMyTuitionHistory() {
        return ApiResponse.<List<StudentTuitionSummaryResponse>>builder()
                .result(tuitionService.getMyTuitionHistory())
                .build();
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TEACHER')")
    public ApiResponse<StudentTuitionSummaryResponse> getStudentTuitionSummary(
            @PathVariable Long studentId,
            @RequestParam(required = false, defaultValue = "1") String semester,
            @RequestParam(required = false, defaultValue = "2024-2025") String academicYear
    ) {
        return ApiResponse.<StudentTuitionSummaryResponse>builder()
                .result(tuitionService.getStudentTuitionSummary(studentId, semester, academicYear))
                .build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<Page<StudentTuitionSummaryResponse>> getAllStudentsTuition(
            @RequestParam(required = false, defaultValue = "1") String semester,
            @RequestParam(required = false, defaultValue = "2024-2025") String academicYear,
            @RequestParam(required = false) Long classGroupId,
            @RequestParam(required = false) TuitionStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort
    ) {
        Sort.Direction direction = sort.length > 1 && "asc".equalsIgnoreCase(sort[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = sort.length > 0 ? sort[0] : "id";
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(direction, sortField));

        return ApiResponse.<Page<StudentTuitionSummaryResponse>>builder()
                .result(tuitionService.getAllStudentsTuition(semester, academicYear, classGroupId, status, search, pageable))
                .build();
    }

    @GetMapping("/dashboard-summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<TuitionDashboardSummaryResponse> getDashboardSummary(
            @RequestParam(required = false, defaultValue = "1") String semester,
            @RequestParam(required = false, defaultValue = "2024-2025") String academicYear
    ) {
        return ApiResponse.<TuitionDashboardSummaryResponse>builder()
                .result(tuitionService.getDashboardSummary(semester, academicYear))
                .build();
    }

    @PostMapping("/record-payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ApiResponse<StudentTuitionSummaryResponse> recordPayment(
            @RequestBody @Valid RecordPaymentRequest request
    ) {
        return ApiResponse.<StudentTuitionSummaryResponse>builder()
                .result(tuitionService.recordPayment(request))
                .build();
    }
}