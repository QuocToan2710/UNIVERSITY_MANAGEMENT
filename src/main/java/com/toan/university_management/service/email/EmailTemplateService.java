package com.toan.university_management.service.email;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class EmailTemplateService {

    private static final String OTP_TEMPLATE_PATH = "email-templates/otp-email.html";
    private static final String ACCOUNT_CREATED_TEMPLATE_PATH = "email-templates/account-created-email.html";

    private final ConcurrentMap<String, String> templateCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        getTemplate(OTP_TEMPLATE_PATH);
        getTemplate(ACCOUNT_CREATED_TEMPLATE_PATH);
    }

    public String buildOtpEmail(String recipientName, String otpCode) {
        String template = getTemplate(OTP_TEMPLATE_PATH);
        if (template != null) {
            return template
                .replace("{{recipientName}}", recipientName != null ? recipientName : "Quý người dùng")
                .replace("{{otpCode}}", otpCode != null ? otpCode : "");
        }
        return "<p>Xin chào " + recipientName + ", mã OTP của bạn là: <b>" + otpCode + "</b></p>";
    }

    public String buildAccountCreatedEmail(String recipientName, String roleTitle, String username, String toEmail, String initialPassword) {
        String template = getTemplate(ACCOUNT_CREATED_TEMPLATE_PATH);
        if (template != null) {
            return template
                .replace("{{recipientName}}", recipientName != null ? recipientName : username)
                .replace("{{roleTitle}}", roleTitle != null ? roleTitle : "Người dùng")
                .replace("{{username}}", username != null ? username : "")
                .replace("{{toEmail}}", toEmail != null ? toEmail : "")
                .replace("{{initialPassword}}", initialPassword != null ? initialPassword : "");
        }
        return "<p>Xin chào " + recipientName + ", tài khoản của bạn: <b>" + username + "</b>, mật khẩu: <b>" + initialPassword + "</b></p>";
    }

    private String getTemplate(String path) {
        return templateCache.computeIfAbsent(path, this::loadTemplate);
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            log.debug("Successfully loaded email template from: {}", path);
            return content;
        } catch (Exception ex) {
            log.warn("Could not load email template from classpath: {}. Using fallback HTML. Error: {}", path, ex.getMessage());
            return null;
        }
    }
}
