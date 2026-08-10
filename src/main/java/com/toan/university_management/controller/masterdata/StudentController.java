package com.toan.university_management.controller.masterdata;


import com.toan.university_management.dto.request.masterdata.StudentRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.StudentResponse;
import com.toan.university_management.service.masterdata.student.StudentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentController {
    StudentService studentService;

    @PostMapping
    ApiResponse<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request){

        return ApiResponse.<StudentResponse>builder()
                .message("Successfully created student")
                .result(studentService.createStudent(request))
                .build();
    }

    @GetMapping
    ApiResponse<org.springframework.data.domain.Page<StudentResponse>> getAllStudents(
            @org.springframework.data.web.PageableDefault(page = 0, size = 10, sort = "id") org.springframework.data.domain.Pageable pageable) {
        return ApiResponse.<org.springframework.data.domain.Page<StudentResponse>>builder()
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
    ApiResponse<StudentResponse> getStudentById(@PathVariable String id) {
        return ApiResponse.<StudentResponse>builder()
                .result(studentService.getStudentById(id))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<StudentResponse> updateStudent(
            @PathVariable String id,
            @Valid @RequestBody StudentRequest request
    ) {
        return ApiResponse.<StudentResponse>builder()
                .result(studentService.updateStudent(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ApiResponse.<String>builder().result("Student has been deleted successfully").build();
    }
}


