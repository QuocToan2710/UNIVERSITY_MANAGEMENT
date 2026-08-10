package com.toan.university_management.controller.masterdata;


import com.toan.university_management.dto.request.masterdata.TeacherRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.TeacherResponse;
import com.toan.university_management.service.masterdata.teacher.TeacherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TeacherController {
    TeacherService teacherService;

    @PostMapping
    ApiResponse<TeacherResponse>  createTeacher(@RequestBody @Valid TeacherRequest request) {
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
    ApiResponse<org.springframework.data.domain.Page<TeacherResponse>> getAllTeachers(
            @org.springframework.data.web.PageableDefault(page = 0, size = 10, sort = "id") org.springframework.data.domain.Pageable pageable) {
        return ApiResponse.<org.springframework.data.domain.Page<TeacherResponse>>builder()
                .result(teacherService.getAllTeachers(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<TeacherResponse>> getAllList() {
        return ApiResponse.<List<TeacherResponse>>builder()
                .result(teacherService.getAllTeachers())
                .build();
    }


    @PutMapping("/{id}")
    ApiResponse<TeacherResponse>  updateTeacher(@PathVariable String id,
                                         @RequestBody @Valid TeacherRequest request) {
        return ApiResponse.<TeacherResponse>builder()
                .result(teacherService.updateTeacher(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteTeacher(@PathVariable String id) {
        teacherService.deleteTeacher(id);
        return ApiResponse.<String>builder().result("Teacher has been deleted successfully").build();
    }

    @GetMapping("/by-specialization")
    public ApiResponse<List<TeacherResponse>> getTeachersBySpecialization(@RequestParam String specialization) {
        return ApiResponse.<List<TeacherResponse>>builder()
                .result(teacherService.getTeachersBySpecialization(specialization))
                .build();
    }

}


