package com.toan.university_management;

import com.toan.university_management.entity.identity.User;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.model.identity.ChangePasswordRequest;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.service.identity.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ChangePasswordTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_USERNAME = "test_student_pw";
    private static final String INITIAL_PASSWORD = "OldPassword@123";

    @BeforeEach
    void setUp() {
        userRepository.findByUsernameAndDeletedFalse(TEST_USERNAME)
                .ifPresent(u -> userRepository.delete(u));

        User user = User.builder()
                .username(TEST_USERNAME)
                .userCode("TEST_PW_01")
                .email("test_pw@university.edu.vn")
                .fullName("Test Student Password")
                .password(passwordEncoder.encode(INITIAL_PASSWORD))
                .build();
        userRepository.save(user);

        // Giả lập user đã đăng nhập vào hệ thống với ROLE_STUDENT
        var auth = new UsernamePasswordAuthenticationToken(
                TEST_USERNAME,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Đổi mật khẩu thành công khi nhập đúng mật khẩu hiện tại")
    void testChangePassword_success() {
        String newPassword = "NewPassword@2026";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword(INITIAL_PASSWORD)
                .newPassword(newPassword)
                .confirmPassword(newPassword)
                .build();

        userService.changePassword(request);

        User updatedUser = userRepository.findByUsernameAndDeletedFalse(TEST_USERNAME).orElseThrow();
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(INITIAL_PASSWORD, updatedUser.getPassword())).isFalse();
    }

    @Test
    @DisplayName("Báo lỗi khi nhập sai mật khẩu hiện tại")
    void testChangePassword_wrongOldPassword_shouldThrowException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword("WrongPassword@999")
                .newPassword("NewPassword@2026")
                .confirmPassword("NewPassword@2026")
                .build();

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OLD_PASSWORD_INCORRECT);
    }

    @Test
    @DisplayName("Báo lỗi khi mật khẩu mới trùng với mật khẩu hiện tại")
    void testChangePassword_samePassword_shouldThrowException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword(INITIAL_PASSWORD)
                .newPassword(INITIAL_PASSWORD)
                .confirmPassword(INITIAL_PASSWORD)
                .build();

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_SAME_AS_OLD);
    }

    @Test
    @DisplayName("Báo lỗi khi mật khẩu xác nhận không khớp")
    void testChangePassword_confirmMismatch_shouldThrowException() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword(INITIAL_PASSWORD)
                .newPassword("NewPassword@2026")
                .confirmPassword("DifferentConfirm@2026")
                .build();

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(AppException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
    }
}
