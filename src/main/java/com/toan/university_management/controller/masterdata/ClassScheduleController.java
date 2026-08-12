package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.ClassScheduleRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.ClassScheduleResponse;
import com.toan.university_management.service.masterdata.schedule.ClassScheduleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassScheduleController {

    ClassScheduleService classScheduleService;

    @PostMapping
    public ApiResponse<ClassScheduleResponse> create(@Valid @RequestBody ClassScheduleRequest request) {
        return ApiResponse.<ClassScheduleResponse>builder()
                .message("Schedule created successfully")
                .result(classScheduleService.createSchedule(request))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<ClassScheduleResponse>> getAll(
            @PageableDefault(page = 0, size = 100, sort = "dayOfWeek") Pageable pageable) {
        return ApiResponse.<Page<ClassScheduleResponse>>builder()
                .result(classScheduleService.getAllSchedules(pageable))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassScheduleResponse> getById(@PathVariable Long id) {
        return ApiResponse.<ClassScheduleResponse>builder()
                .result(classScheduleService.getScheduleById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ClassScheduleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ClassScheduleRequest request) {
        return ApiResponse.<ClassScheduleResponse>builder()
                .message("Schedule updated successfully")
                .result(classScheduleService.updateSchedule(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        classScheduleService.deleteSchedule(id);
        return ApiResponse.<String>builder()
                .result("Schedule deleted successfully")
                .build();
    }

    @GetMapping("/teacher/{teacherId}")
    public ApiResponse<List<ClassScheduleResponse>> getByTeacher(
            @PathVariable Long teacherId,
            @RequestParam String semester,
            @RequestParam String academicYear) {
        return ApiResponse.<List<ClassScheduleResponse>>builder()
                .result(classScheduleService.getByTeacher(teacherId, semester, academicYear))
                .build();
    }

    @GetMapping("/class-group/{classGroupId}")
    public ApiResponse<List<ClassScheduleResponse>> getByClassGroup(
            @PathVariable Long classGroupId,
            @RequestParam String semester,
            @RequestParam String academicYear) {
        return ApiResponse.<List<ClassScheduleResponse>>builder()
                .result(classScheduleService.getByClassGroup(classGroupId, semester, academicYear))
                .build();
    }

    @GetMapping({"/subject/{subjectId}", "/course/{subjectId}"})
    public ApiResponse<List<ClassScheduleResponse>> getBySubject(
            @PathVariable Long subjectId,
            @RequestParam String semester,
            @RequestParam String academicYear) {
        return ApiResponse.<List<ClassScheduleResponse>>builder()
                .result(classScheduleService.getBySubject(subjectId, semester, academicYear))
                .build();
    }

    @GetMapping("/student/{studentId}")
    public ApiResponse<List<ClassScheduleResponse>> getByStudent(
            @PathVariable Long studentId,
            @RequestParam String semester,
            @RequestParam String academicYear) {
        return ApiResponse.<List<ClassScheduleResponse>>builder()
                .result(classScheduleService.getByStudent(studentId, semester, academicYear))
                .build();
    }

    @GetMapping("/my")
    public ApiResponse<List<ClassScheduleResponse>> getMySchedule(
            @RequestParam(required = false, defaultValue = "HK1") String semester,
            @RequestParam(required = false, defaultValue = "2025-2026") String academicYear) {
        return ApiResponse.<List<ClassScheduleResponse>>builder()
                .result(classScheduleService.getMySchedule(semester, academicYear))
                .build();
    }
}
