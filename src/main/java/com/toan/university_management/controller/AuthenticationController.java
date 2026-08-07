package com.toan.university_management.controller;


import com.nimbusds.jose.JOSEException;
import com.toan.university_management.annotation.PermissionMeta;
import com.toan.university_management.dto.request.AuthenticationRequest;
import com.toan.university_management.dto.request.IntrospectRequest;
import com.toan.university_management.dto.request.LogoutRequest;
import com.toan.university_management.dto.request.RefreshRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.AuthenticationResponse;
import com.toan.university_management.dto.response.IntrospectResponse;
import com.toan.university_management.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PermissionMeta(module = "AUTH")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PermissionMeta(value = "Đăng nhập hệ thống", isPublic = true)
    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        var result =  authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PermissionMeta(value = "Kiểm tra token", isPublic = true)
    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request) {
        var result =  authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PermissionMeta(value = "Đăng xuất", isPublic = true)
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PermissionMeta(value = "Làm mới token", isPublic = true)
    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request) {
        var result =  authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

}
