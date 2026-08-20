package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.StudentRequest;
import com.toan.university_management.dto.request.masterdata.StudentSearchPaginationRQ;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.BasePaginationRS;
import com.toan.university_management.dto.response.masterdata.StudentResponse;
import com.toan.university_management.service.masterdata.student.StudentService;
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
@RequestMapping("/students")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentController {
    StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    ApiResponse<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        return ApiResponse.<StudentResponse>builder()
                .message("Successfully created student")
                .result(studentService.createStudent(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<StudentResponse>> getAllStudents(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<StudentResponse>>builder()
                .result(studentService.getAllStudents(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<StudentResponse>> getAllList() {
        return ApiResponse.<List<StudentResponse>>builder()
                .result(studentService.getAllStudents())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<StudentResponse> getStudentById(@PathVariable Long id) {
        return ApiResponse.<StudentResponse>builder()
                .result(studentService.getStudentById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    ApiResponse<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ApiResponse.<StudentResponse>builder()
                .result(studentService.updateStudent(id, request))
                .build();
    }

    @PostMapping("/search")
    ApiResponse<BasePaginationRS<StudentResponse>> searchStudents(@RequestBody(required = false) StudentSearchPaginationRQ request) {
        return ApiResponse.<BasePaginationRS<StudentResponse>>builder()
                .result(studentService.search(request))
                .build();
    }

    @PostMapping("/export")
    ApiResponse<List<StudentResponse>> exportStudents(@RequestBody(required = false) StudentSearchPaginationRQ request) {
        return ApiResponse.<List<StudentResponse>>builder()
                .result(studentService.export(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ApiResponse.<String>builder().result("Student has been deleted successfully").build();
    }
}
