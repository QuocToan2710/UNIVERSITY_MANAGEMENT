package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.ExamScheduleRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.ExamScheduleResponse;
import com.toan.university_management.service.masterdata.examschedule.ExamScheduleService;
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
@RequestMapping("/exam-schedules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExamScheduleController {
    ExamScheduleService examScheduleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ExamScheduleResponse> createExamSchedule(@Valid @RequestBody ExamScheduleRequest request) {
        return ApiResponse.<ExamScheduleResponse>builder()
                .message("Successfully created exam schedule")
                .result(examScheduleService.createExamSchedule(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<ExamScheduleResponse>> getAllExamSchedules(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<ExamScheduleResponse>>builder()
                .result(examScheduleService.getAllExamSchedules(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<ExamScheduleResponse>> getAllList() {
        return ApiResponse.<List<ExamScheduleResponse>>builder()
                .result(examScheduleService.getAllExamSchedules())
                .build();
    }

    @GetMapping("/my")
    ApiResponse<List<ExamScheduleResponse>> getMyExamSchedules() {
        return ApiResponse.<List<ExamScheduleResponse>>builder()
                .result(examScheduleService.getMyExamSchedules())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ExamScheduleResponse> getExamScheduleById(@PathVariable Long id) {
        return ApiResponse.<ExamScheduleResponse>builder()
                .result(examScheduleService.getExamScheduleById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ExamScheduleResponse> updateExamSchedule(@PathVariable Long id, @Valid @RequestBody ExamScheduleRequest request) {
        return ApiResponse.<ExamScheduleResponse>builder()
                .result(examScheduleService.updateExamSchedule(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteExamSchedule(@PathVariable Long id) {
        examScheduleService.deleteExamSchedule(id);
        return ApiResponse.<String>builder().result("Exam schedule has been deleted successfully").build();
    }
}
