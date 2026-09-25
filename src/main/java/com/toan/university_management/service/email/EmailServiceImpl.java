package com.toan.university_management.service.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class EmailServiceImpl implements EmailService {

    final ObjectProvider<JavaMailSender> mailSenderProvider;
    final ObjectMapper objectMapper;
    final EmailTemplateService templateService;

    @Value("${app.mail.from-email:${spring.mail.username:phamtoan27102003@gmail.com}}")
    String fromEmail;

    @Value("${app.mail.from-name:Hệ Thống Đào Tạo Đại Học}")
    String fromName;

    @Value("${app.mail.brevo-api-key:${BREVO_API_KEY:}}")
    String brevoApiKey;

    private String resolveSenderEmail() {
        if (fromEmail != null && fromEmail.contains("@")) {
            return fromEmail;
        }
        return "phamtoan27102003@gmail.com";
    }

    private String resolveSenderName() {
        if (fromName != null && !fromName.isBlank()) {
            return fromName;
        }
        return "Hệ Thống Đào Tạo Đại Học";
    }

    private void sendHtmlEmail(String toEmail, String recipientName, String subject, String htmlContent) throws Exception {
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            try {
                sendViaBrevoApi(toEmail, recipientName, subject, htmlContent);
                return;
            } catch (Exception ex) {
                log.warn("Failed to send email via Brevo REST API: {}. Falling back to SMTP...", ex.getMessage());
            }
        }
        sendViaSmtp(toEmail, subject, htmlContent);
    }

    private void sendViaBrevoApi(String toEmail, String recipientName, String subject, String htmlContent) throws Exception {
        String senderEmail = resolveSenderEmail();
        String senderName = resolveSenderName();

        Map<String, Object> payload = Map.of(
            "sender", Map.of("name", senderName, "email", senderEmail),
            "to", List.of(Map.of("email", toEmail, "name", (recipientName != null && !recipientName.isBlank()) ? recipientName : toEmail)),
            "subject", subject,
            "htmlContent", htmlContent
        );

        String jsonBody = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
            .header("api-key", brevoApiKey.trim())
            .header("Content-Type", "application/json")
            .header("accept", "application/json")
            .timeout(Duration.ofSeconds(10))
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
            .build();

        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            log.info("Successfully sent email via Brevo REST API (HTTPS 443) to: {}", toEmail);
        } else {
            throw new RuntimeException("Brevo API HTTP " + statusCode + ": " + response.body());
        }
    }

    private void sendViaSmtp(String toEmail, String subject, String htmlContent) throws Exception {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("JavaMailSender is not configured in this environment. Skipping SMTP send for: {}", toEmail);
            return;
        }
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(resolveSenderEmail(), resolveSenderName());
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        log.info("Successfully sent email via SMTP to: {}", toEmail);
    }

    @Override
    public void sendOtpEmail(String toEmail, String recipientName, String otpCode) {
        String safeName = (recipientName != null && !recipientName.isBlank()) ? recipientName : "Quý người dùng";
        String subject = "[Đại học] Mã xác nhận khôi phục mật khẩu: " + otpCode;
        String htmlContent = templateService.buildOtpEmail(safeName, otpCode);

        try {
            sendHtmlEmail(toEmail, safeName, subject, htmlContent);
        } catch (Exception ex) {
            log.warn("Could not send email via Brevo API or SMTP (using fallback log). To: {}, OTP: {}, Error: {}", toEmail, otpCode, ex.getMessage());
            log.info("[DEV/FALLBACK OTP] Email: {} | OTP Code: {} (Expires in 10 mins)", toEmail, otpCode);
        }
    }

    @Override
    public void sendAccountCreatedEmail(String toEmail, String recipientName, String username, String initialPassword, String roleName) {
        String safeName = (recipientName != null && !recipientName.isBlank()) ? recipientName : username;
        String roleTitle = ("ROLE_STUDENT".equalsIgnoreCase(roleName) || "STUDENT".equalsIgnoreCase(roleName)) ? "Sinh viên" : "Giảng viên";
        String subject = "[Đại học] Cấp tài khoản đăng nhập " + roleTitle + " - " + username;
        String htmlContent = templateService.buildAccountCreatedEmail(safeName, roleTitle, username, toEmail, initialPassword);

        try {
            sendHtmlEmail(toEmail, safeName, subject, htmlContent);
        } catch (Exception ex) {
            log.warn("Could not send credentials email via Brevo API or SMTP (using fallback log). To: {}, Error: {}", toEmail, ex.getMessage());
            log.info("[DEV/FALLBACK WELCOME EMAIL] Email: {} | Username: {} | Password: {}", toEmail, username, initialPassword);
        }
    }
}
