package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.TeacherRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.TeacherResponse;
import com.toan.university_management.service.masterdata.teacher.TeacherService;
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
@RequestMapping("/teachers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherController {
    TeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<TeacherResponse> createTeacher(@Valid @RequestBody TeacherRequest request) {
        return ApiResponse.<TeacherResponse>builder()
                .message("Successfully created teacher")
                .result(teacherService.createTeacher(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<TeacherResponse>> getAllTeachers(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<TeacherResponse>>builder()
                .result(teacherService.getAllTeachers(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<TeacherResponse>> getAllList() {
        return ApiResponse.<List<TeacherResponse>>builder()
                .result(teacherService.getAllTeachers())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<TeacherResponse> getTeacherById(@PathVariable Long id) {
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.getTeacherById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<TeacherResponse> updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherRequest request) {
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.updateTeacher(id, request))
                .build();
    }

    @PostMapping("/search")
    ApiResponse<com.toan.university_management.dto.response.BasePaginationRS<TeacherResponse>> searchTeachers(
            @RequestBody(required = false) com.toan.university_management.dto.request.masterdata.TeacherSearchPaginationRQ request) {
        return ApiResponse.<com.toan.university_management.dto.response.BasePaginationRS<TeacherResponse>>builder()
                .result(teacherService.search(request))
                .build();
    }

    @PostMapping("/export")
    ApiResponse<List<TeacherResponse>> exportTeachers(
            @RequestBody(required = false) com.toan.university_management.dto.request.masterdata.TeacherSearchPaginationRQ request) {
        return ApiResponse.<List<TeacherResponse>>builder()
                .result(teacherService.export(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ApiResponse.<String>builder().result("Teacher has been deleted successfully").build();
    }
}
