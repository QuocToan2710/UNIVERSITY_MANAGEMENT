package com.toan.university_management.controller.auth;


import com.nimbusds.jose.JOSEException;
import com.toan.university_management.annotation.PermissionMeta;
import com.toan.university_management.dto.request.auth.AuthenticationRequest;
import com.toan.university_management.dto.request.auth.IntrospectRequest;
import com.toan.university_management.dto.request.auth.LogoutRequest;
import com.toan.university_management.dto.request.auth.RefreshRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.auth.AuthenticationResponse;
import com.toan.university_management.dto.response.auth.IntrospectResponse;
import com.toan.university_management.service.auth.AuthenticationService;
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

    @PermissionMeta(value = "Yêu cầu khôi phục mật khẩu qua Email", isPublic = true)
    @PostMapping("/forgot-password")
    ApiResponse<String> forgotPassword(@RequestBody @jakarta.validation.Valid com.toan.university_management.dto.request.auth.ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);
        return ApiResponse.<String>builder()
                .result("Mã xác nhận OTP đã được gửi đến địa chỉ email của bạn.")
                .build();
    }

    @PermissionMeta(value = "Xác nhận OTP và đặt lại mật khẩu", isPublic = true)
    @PostMapping("/reset-password")
    ApiResponse<String> resetPassword(@RequestBody @jakarta.validation.Valid com.toan.university_management.dto.request.auth.ResetPasswordRequest request) {
        authenticationService.resetPassword(request);
        return ApiResponse.<String>builder()
                .result("Đặt lại mật khẩu thành công. Vui lòng đăng nhập bằng mật khẩu mới.")
                .build();
    }
}


