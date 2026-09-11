package com.toan.university_management.controller.identity;


import com.toan.university_management.dto.request.identity.UserRequest;
import com.toan.university_management.common.dto.ApiResponse;
import com.toan.university_management.dto.response.identity.UserResponse;
import com.toan.university_management.service.identity.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<org.springframework.data.domain.Page<UserResponse>> getUsers(
            @org.springframework.data.web.PageableDefault(page = 0, size = 10, sort = "id") org.springframework.data.domain.Pageable pageable) {
        return ApiResponse.<org.springframework.data.domain.Page<UserResponse>>builder()
                .result(userService.getAllUsers(pageable))
                .build();
    }

    @GetMapping("/myInfo")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") Long userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @PutMapping("update")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<UserResponse> updateUser(@RequestBody UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(request))
                .build();
    }

    @PutMapping("/{userId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<UserResponse> updateUserRoles(@PathVariable("userId") Long userId, @RequestBody List<String> roleNames) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserRoles(userId, roleNames))
                .build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder().result("User has been deleted successfully").build();
    }
}


