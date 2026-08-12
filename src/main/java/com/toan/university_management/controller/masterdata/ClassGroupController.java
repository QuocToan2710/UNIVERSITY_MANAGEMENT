package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.ClassGroupRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.ClassGroupResponse;
import com.toan.university_management.service.masterdata.classgroup.ClassGroupService;
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
@RequestMapping("/class-groups")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClassGroupController {
    ClassGroupService classGroupService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ClassGroupResponse> createClassGroup(@Valid @RequestBody ClassGroupRequest request) {
        return ApiResponse.<ClassGroupResponse>builder()
                .message("Successfully created class group")
                .result(classGroupService.createClassGroup(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<ClassGroupResponse>> getAllClassGroups(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<ClassGroupResponse>>builder()
                .result(classGroupService.getAllClassGroups(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<ClassGroupResponse>> getAllList() {
        return ApiResponse.<List<ClassGroupResponse>>builder()
                .result(classGroupService.getAllClassGroups())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ClassGroupResponse> getClassGroupById(@PathVariable Long id) {
        return ApiResponse.<ClassGroupResponse>builder()
                .result(classGroupService.getClassGroupById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ClassGroupResponse> updateClassGroup(@PathVariable Long id, @Valid @RequestBody ClassGroupRequest request) {
        return ApiResponse.<ClassGroupResponse>builder()
                .result(classGroupService.updateClassGroup(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteClassGroup(@PathVariable Long id) {
        classGroupService.deleteClassGroup(id);
        return ApiResponse.<String>builder().result("Class group has been deleted successfully").build();
    }
}
