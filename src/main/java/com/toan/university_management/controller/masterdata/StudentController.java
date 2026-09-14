package com.toan.university_management.controller.masterdata;

import com.toan.university_management.common.controller.BaseController;
import com.toan.university_management.model.masterdata.StudentRequest;
import com.toan.university_management.model.masterdata.StudentSearchPaginationRQ;
import com.toan.university_management.common.dto.ApiResponse;
import com.toan.university_management.common.dto.BasePaginationRS;
import com.toan.university_management.model.masterdata.StudentResponse;
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
public class StudentController extends BaseController {

    StudentService studentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    ApiResponse<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        return success(studentService.createStudent(request), "Successfully created student");
    }

    @GetMapping
    ApiResponse<Page<StudentResponse>> getAllStudents(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return success(studentService.getAllStudents(pageable));
    }

    @GetMapping("/all")
    ApiResponse<List<StudentResponse>> getAllList() {
        return success(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    ApiResponse<StudentResponse> getStudentById(@PathVariable Long id) {
        return success(studentService.getStudentById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    ApiResponse<StudentResponse> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return success(studentService.updateStudent(id, request), "Successfully updated student");
    }

    @PostMapping("/search")
    ApiResponse<BasePaginationRS<StudentResponse>> searchStudents(@RequestBody(required = false) StudentSearchPaginationRQ request) {
        return paginate(studentService.search(request));
    }

    @PostMapping("/export")
    ApiResponse<List<StudentResponse>> exportStudents(@RequestBody(required = false) StudentSearchPaginationRQ request) {
        return success(studentService.export(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return success("Student has been deleted successfully");
    }
}
