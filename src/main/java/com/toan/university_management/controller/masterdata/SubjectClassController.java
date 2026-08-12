package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.SubjectClassRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.SubjectClassResponse;
import com.toan.university_management.service.masterdata.subjectclass.SubjectClassService;
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
@RequestMapping({"/subject-classes", "/course-classes", "/courses"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectClassController {
    SubjectClassService subjectClassService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<SubjectClassResponse> createSubjectClass(@Valid @RequestBody SubjectClassRequest request) {
        return ApiResponse.<SubjectClassResponse>builder()
                .message("Successfully created subject class")
                .result(subjectClassService.createSubjectClass(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<SubjectClassResponse>> getAllSubjectClasses(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<SubjectClassResponse>>builder()
                .result(subjectClassService.getAllSubjectClasses(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<SubjectClassResponse>> getAllList() {
        return ApiResponse.<List<SubjectClassResponse>>builder()
                .result(subjectClassService.getAllSubjectClasses())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<SubjectClassResponse> getSubjectClassById(@PathVariable Long id) {
        return ApiResponse.<SubjectClassResponse>builder()
                .result(subjectClassService.getSubjectClassById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<SubjectClassResponse> updateSubjectClass(@PathVariable Long id, @Valid @RequestBody SubjectClassRequest request) {
        return ApiResponse.<SubjectClassResponse>builder()
                .result(subjectClassService.updateSubjectClass(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteSubjectClass(@PathVariable Long id) {
        subjectClassService.deleteSubjectClass(id);
        return ApiResponse.<String>builder().result("Subject class has been deleted successfully").build();
    }
}
