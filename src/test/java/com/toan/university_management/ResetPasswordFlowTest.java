package com.toan.university_management;

import com.toan.university_management.entity.identity.User;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.model.auth.ForgotPasswordRequest;
import com.toan.university_management.model.auth.ResetPasswordRequest;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.service.auth.AuthenticationService;
import com.toan.university_management.service.otp.OtpService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ResetPasswordFlowTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    private static final String TEST_EMAIL = "forgot_test@university.edu.vn";
    private static final String TEST_USERNAME = "sv_forgot_01";
    private static final String INITIAL_PASSWORD = "InitialPassword@123";

    @BeforeEach
    void setUp() {
        userRepository.findByEmailAndDeletedFalse(TEST_EMAIL)
                .ifPresent(u -> userRepository.delete(u));

        User user = User.builder()
                .username(TEST_USERNAME)
                .userCode("SV_FORGOT_01")
                .email(TEST_EMAIL)
                .fullName("Sinh Vien Test Forgot")
                .password(passwordEncoder.encode(INITIAL_PASSWORD))
                .build();
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        otpService.clearOtp(TEST_EMAIL);
    }

    @Test
    @DisplayName("Quên mật khẩu & Đặt lại mật khẩu qua OTP thành công")
    void testForgotAndResetPassword_success() {
        // 1. Gọi forgotPassword
        ForgotPasswordRequest forgotReq = ForgotPasswordRequest.builder()
                .email(TEST_EMAIL)
                .build();
        authenticationService.forgotPassword(forgotReq);

        // Giả lập sinh mã OTP hoặc lấy mã OTP đã được tạo
        String otp = otpService.generateAndStoreOtp(TEST_EMAIL);

        // 2. Gọi resetPassword với OTP đúng
        String newPassword = "BrandNewPassword@2026";
        ResetPasswordRequest resetReq = ResetPasswordRequest.builder()
                .email(TEST_EMAIL)
                .otp(otp)
                .newPassword(newPassword)
                .build();

        authenticationService.resetPassword(resetReq);

        // 3. Kiểm tra User trong DB đã được cập nhật mật khẩu mới
        User updatedUser = userRepository.findByEmailAndDeletedFalse(TEST_EMAIL).orElseThrow();
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(INITIAL_PASSWORD, updatedUser.getPassword())).isFalse();

        // 4. Mã OTP đã bị xóa khỏi cache
        assertThat(otpService.verifyOtp(TEST_EMAIL, otp)).isFalse();
    }

    @Test
    @DisplayName("Đặt lại mật khẩu thất bại khi nhập sai OTP")
    void testResetPassword_wrongOtp_shouldThrowException() {
        otpService.generateAndStoreOtp(TEST_EMAIL);

        ResetPasswordRequest resetReq = ResetPasswordRequest.builder()
                .email(TEST_EMAIL)
                .otp("000000") // Sai OTP
                .newPassword("NewPassword@2026")
                .build();

        assertThatThrownBy(() -> authenticationService.resetPassword(resetReq))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OTP_INVALID);
    }
}
