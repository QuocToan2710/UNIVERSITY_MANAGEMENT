package com.toan.university_management.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder(10);

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                // Tắt hash: Lưu mật khẩu mới/đổi mật khẩu dưới dạng chuỗi thuần (plain-text)
                return rawPassword != null ? rawPassword.toString() : null;
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (rawPassword == null || encodedPassword == null) {
                    return false;
                }
                // 1. Khớp chuỗi thuần trực tiếp (plain text)
                if (rawPassword.toString().equals(encodedPassword)) {
                    return true;
                }
                // 2. Tương thích ngược: Nếu mật khẩu cũ trong DB còn lưu dạng hash BCrypt ($2a$, $2b$), vẫn đăng nhập bình thường
                if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$") || encodedPassword.startsWith("$2y$")) {
                    try {
                        return BCRYPT.matches(rawPassword, encodedPassword);
                    } catch (Exception ignored) {
                        return false;
                    }
                }
                return false;
            }
        };
    }
}
