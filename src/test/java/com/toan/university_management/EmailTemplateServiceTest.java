package com.toan.university_management;

import com.toan.university_management.service.email.EmailTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailTemplateServiceTest {

    private EmailTemplateService emailTemplateService;

    @BeforeEach
    void setUp() {
        emailTemplateService = new EmailTemplateService();
        emailTemplateService.init();
    }

    @Test
    @DisplayName("buildOtpEmail should substitute placeholders with values")
    void buildOtpEmail_shouldSubstitutePlaceholders() {
        String html = emailTemplateService.buildOtpEmail("Nguyễn Văn A", "123456");

        assertThat(html).isNotNull();
        assertThat(html).contains("Nguyễn Văn A");
        assertThat(html).contains("123456");
        assertThat(html).doesNotContain("{{recipientName}}");
        assertThat(html).doesNotContain("{{otpCode}}");
    }

    @Test
    @DisplayName("buildOtpEmail should handle null recipientName gracefully")
    void buildOtpEmail_withNullRecipient_shouldUseDefault() {
        String html = emailTemplateService.buildOtpEmail(null, "654321");

        assertThat(html).isNotNull();
        assertThat(html).contains("Quý người dùng");
        assertThat(html).contains("654321");
    }

    @Test
    @DisplayName("buildAccountCreatedEmail should substitute all credentials correctly")
    void buildAccountCreatedEmail_shouldSubstituteAllCredentials() {
        String html = emailTemplateService.buildAccountCreatedEmail(
            "Trần Thị B",
            "Sinh viên",
            "SV2026001",
            "tranb@example.com",
            "P@ssw0rd123"
        );

        assertThat(html).isNotNull();
        assertThat(html).contains("Trần Thị B");
        assertThat(html).contains("Sinh viên");
        assertThat(html).contains("SV2026001");
        assertThat(html).contains("tranb@example.com");
        assertThat(html).contains("P@ssw0rd123");
        assertThat(html).contains("ĐĂNG NHẬP HỆ THỐNG");
        assertThat(html).doesNotContain("{{username}}");
        assertThat(html).doesNotContain("{{toEmail}}");
        assertThat(html).doesNotContain("{{initialPassword}}");
    }
}
