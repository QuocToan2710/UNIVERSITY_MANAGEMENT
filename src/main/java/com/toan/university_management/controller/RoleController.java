package com.toan.university_management.controller;


import com.toan.university_management.dto.request.RoleRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.RoleResponse;
import com.toan.university_management.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRole())
                .build();
    }

    @PutMapping("/{roleName}/permissions")
    ApiResponse<RoleResponse> updatePermissions(@PathVariable String roleName, @RequestBody java.util.Set<String> permissions) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.updateRolePermissions(roleName, permissions))
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable String role){
        roleService.deleteRole(role);
        return ApiResponse.<Void>builder().build();
    }
}
