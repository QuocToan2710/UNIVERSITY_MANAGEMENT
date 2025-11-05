package com.toan.university_management.controller;


import com.toan.university_management.dto.request.PermissionRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.PermissionResponse;
import com.toan.university_management.service.implement.PermissionSeviceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionSeviceImpl permissionSevice;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request){
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionSevice.createPermission(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponse>> getAll(){
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionSevice.getAllPermission())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable String permission){
        permissionSevice.deletePermission(permission);
        return ApiResponse.<Void>builder().build();
    }
}
