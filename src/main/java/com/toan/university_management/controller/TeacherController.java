package com.toan.university_management.controller;


import com.toan.university_management.dto.request.TeacherRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.TeacherResponse;
import com.toan.university_management.service.implement.TeacherServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherController {
    TeacherServiceImpl teacherService;

    @PostMapping
    ApiResponse<TeacherResponse>  createTeacher(@RequestBody TeacherRequest request) {
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.createTeacher(request))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<TeacherResponse>  getTeacherById(@PathVariable String id) {
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.getTeacherById(id))
                .build();
    }

    @GetMapping
    ApiResponse<List<TeacherResponse>>  getAllTeachers() {
        return ApiResponse.<List<TeacherResponse>>builder()
                .result(teacherService.getAllTeachers())
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<TeacherResponse>  updateTeacher(@PathVariable String id,
                                         @RequestBody TeacherRequest request) {
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.updateTeacher(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteTeacher(@PathVariable String id) {
        teacherService.deleteTeacher(id);
        return ApiResponse.<String>builder().result("Teacher has been delete").build();
    }

    @GetMapping("/by-specialization")
    public ApiResponse<List<TeacherResponse>> getTeachersBySpecialization(@RequestParam String specialization) {
        return ApiResponse.<List<TeacherResponse>>builder()
                .result(teacherService.getTeachersBySpecialization(specialization))
                .build();
    }

}
