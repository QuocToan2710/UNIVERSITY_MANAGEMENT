package com.toan.university_management.controller.masterdata;


import com.toan.university_management.dto.request.masterdata.CourseRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.CourseResponse;
import com.toan.university_management.service.masterdata.course.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CourseController {
    CourseService courseService;

    @PostMapping
    ApiResponse<CourseResponse>  create(@RequestBody @Valid CourseRequest request) {
        return ApiResponse.<CourseResponse>builder()
                .message("Successfully created course")
                .result(courseService.createCourse(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<CourseResponse>  update(@PathVariable String id, @RequestBody @Valid CourseRequest request) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.updateCourse(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> delete(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ApiResponse.<String>builder().result("Course has been deleted successfully").build();
    }

    @GetMapping("/{id}")
    ApiResponse<CourseResponse> getById(@PathVariable String id) {
        return ApiResponse.<CourseResponse>builder()
                .result(courseService.getCourseById(id))
                .build();
    }

    @GetMapping
    ApiResponse<org.springframework.data.domain.Page<CourseResponse>> getAll(
            @org.springframework.data.web.PageableDefault(page = 0, size = 10, sort = "id") org.springframework.data.domain.Pageable pageable) {
        return ApiResponse.<org.springframework.data.domain.Page<CourseResponse>>builder()
                .message("Successfully get course ")
                .result(courseService.getAllCourses(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<CourseResponse>> getAllList() {
        return ApiResponse.<List<CourseResponse>>builder()
                .message("Successfully get all courses")
                .result(courseService.getAllCourses())
                .build();
    }


    @GetMapping("/byteacher")
    ApiResponse<List<CourseResponse>> getCoursesByTeacherName(@RequestParam("name") String teacherName) {
        return ApiResponse.<List<CourseResponse>>builder()
                .message("Successfully get course")
                .result(courseService.getCoursesByTeacherName(teacherName))
                .build();
    }
}


