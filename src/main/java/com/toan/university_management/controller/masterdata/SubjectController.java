package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.SubjectRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.SubjectResponse;
import com.toan.university_management.service.masterdata.subject.SubjectService;
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
@RequestMapping({"/subjects", "/subject-list"})
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubjectController {
    SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<SubjectResponse> createSubject(@Valid @RequestBody SubjectRequest request) {
        return ApiResponse.<SubjectResponse>builder()
                .message("Successfully created subject")
                .result(subjectService.createSubject(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<SubjectResponse>> getAllSubjects(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<SubjectResponse>>builder()
                .result(subjectService.getAllSubjects(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<SubjectResponse>> getAllList() {
        return ApiResponse.<List<SubjectResponse>>builder()
                .result(subjectService.getAllSubjects())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<SubjectResponse> getSubjectById(@PathVariable Long id) {
        return ApiResponse.<SubjectResponse>builder()
                .result(subjectService.getSubjectById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<SubjectResponse> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
        return ApiResponse.<SubjectResponse>builder()
                .result(subjectService.updateSubject(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ApiResponse.<String>builder().result("Subject has been deleted successfully").build();
    }
}
