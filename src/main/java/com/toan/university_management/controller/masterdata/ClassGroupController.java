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
    public ApiResponse<ClassGroupResponse> create(@Valid @RequestBody ClassGroupRequest request) {
        return ApiResponse.<ClassGroupResponse>builder()
                .message("Class group created successfully")
                .result(classGroupService.createClassGroup(request))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<ClassGroupResponse>> getAll(
            @PageableDefault(page = 0, size = 20, sort = "classCode") Pageable pageable) {
        return ApiResponse.<Page<ClassGroupResponse>>builder()
                .result(classGroupService.getAllClassGroups(pageable))
                .build();
    }

    /** Endpoint không phân trang — dùng cho dropdown trong form */
    @GetMapping("/all")
    public ApiResponse<List<ClassGroupResponse>> getAllList() {
        return ApiResponse.<List<ClassGroupResponse>>builder()
                .result(classGroupService.getAllClassGroups())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ClassGroupResponse> getById(@PathVariable String id) {
        return ApiResponse.<ClassGroupResponse>builder()
                .result(classGroupService.getClassGroupById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ClassGroupResponse> update(
            @PathVariable String id,
            @Valid @RequestBody ClassGroupRequest request) {
        return ApiResponse.<ClassGroupResponse>builder()
                .message("Class group updated successfully")
                .result(classGroupService.updateClassGroup(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable String id) {
        classGroupService.deleteClassGroup(id);
        return ApiResponse.<String>builder()
                .result("Class group deleted successfully")
                .build();
    }
}


