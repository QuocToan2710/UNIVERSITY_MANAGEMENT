package com.toan.university_management.service.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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

    final JavaMailSender mailSender;
    final ObjectMapper objectMapper;

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

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; color: #333; }
                    .container { max-width: 540px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #1e3a8a, #3b82f6); padding: 30px; text-align: center; color: #ffffff; }
                    .header h1 { margin: 0; font-size: 22px; font-weight: 700; letter-spacing: 0.5px; }
                    .content { padding: 30px; line-height: 1.6; }
                    .otp-box { background-color: #eff6ff; border: 2px dashed #3b82f6; border-radius: 8px; text-align: center; padding: 18px; margin: 24px 0; }
                    .otp-code { font-size: 32px; font-weight: 800; color: #1d4ed8; letter-spacing: 6px; }
                    .warning { font-size: 13px; color: #64748b; margin-top: 15px; }
                    .footer { background-color: #f8fafc; padding: 20px; text-align: center; font-size: 12px; color: #94a3b8; border-top: 1px solid #e2e8f0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>CỔNG THÔNG TIN ĐÀO TẠO ĐẠI HỌC</h1>
                    </div>
                    <div class="content">
                        <p>Xin chào <strong>%s</strong>,</p>
                        <p>Bạn đã gửi yêu cầu đặt lại mật khẩu cho tài khoản liên kết với địa chỉ email này. Dưới đây là mã xác thực OTP của bạn:</p>
                        <div class="otp-box">
                            <div class="otp-code">%s</div>
                            <div class="warning">Mã xác thực có hiệu lực trong <strong>10 phút</strong></div>
                        </div>
                        <p>Nếu bạn không yêu cầu hành động này, vui lòng bỏ qua email hoặc liên hệ với Phòng Đào tạo / Ban Quản trị Hệ thống để được hỗ trợ bảo mật.</p>
                    </div>
                    <div class="footer">
                        <p>© 2026 Hệ thống Quản trị Đại học. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(safeName, otpCode);

        try {
            sendHtmlEmail(toEmail, safeName, subject, htmlContent);
        } catch (Exception ex) {
            log.warn("Could not send email via Brevo API or SMTP (using fallback log). To: {}, OTP: {}, Error: {}", toEmail, otpCode, ex.getMessage());
            log.info("🔑 [DEV/FALLBACK OTP] Email: {} | OTP Code: {} (Expires in 10 mins)", toEmail, otpCode);
        }
    }

    @Override
    public void sendAccountCreatedEmail(String toEmail, String recipientName, String username, String initialPassword, String roleName) {
        String safeName = (recipientName != null && !recipientName.isBlank()) ? recipientName : username;
        String roleTitle = ("ROLE_STUDENT".equalsIgnoreCase(roleName) || "STUDENT".equalsIgnoreCase(roleName)) ? "Sinh viên" : "Giảng viên";
        String subject = "[Đại học] Cấp tài khoản đăng nhập " + roleTitle + " - " + username;

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f7f6; margin: 0; padding: 20px; color: #333; }
                    .container { max-width: 560px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #1e3a8a, #0284c7); padding: 28px; text-align: center; color: #ffffff; }
                    .header h1 { margin: 0; font-size: 20px; font-weight: 700; }
                    .content { padding: 30px; line-height: 1.6; }
                    .info-box { background-color: #f8fafc; border: 1px solid #e2e8f0; border-radius: 8px; padding: 18px; margin: 20px 0; }
                    .info-row { display: flex; justify-content: space-between; margin-bottom: 10px; font-size: 14px; }
                    .info-row:last-child { margin-bottom: 0; }
                    .info-label { color: #64748b; font-weight: 600; }
                    .info-value { color: #0f172a; font-weight: 700; }
                    .highlight { color: #2563eb; font-family: monospace; font-size: 15px; }
                    .warning { font-size: 12.5px; color: #b45309; background-color: #fffbeb; border: 1px solid #fde68a; border-radius: 6px; padding: 10px 14px; margin-top: 15px; }
                    .footer { background-color: #f8fafc; padding: 18px; text-align: center; font-size: 12px; color: #94a3b8; border-top: 1px solid #e2e8f0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>THÔNG BÁO CẤP TÀI KHOẢN HỆ THỐNG ĐÀO TẠO</h1>
                    </div>
                    <div class="content">
                        <p>Kính gửi: <strong>%s</strong>,</p>
                        <p>Hồ sơ <strong>%s</strong> của bạn đã được khởi tạo thành công trên hệ thống. Dưới đây là thông tin tài khoản đăng nhập chính thức của bạn:</p>
                        <div class="info-box">
                            <div class="info-row">
                                <span class="info-label">Tên đăng nhập / Mã:</span>
                                <span class="info-value highlight">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Email tài khoản:</span>
                                <span class="info-value">%s</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">Mật khẩu khởi tạo:</span>
                                <span class="info-value highlight">%s</span>
                            </div>
                        </div>
                        <div class="warning">
                            ⚠️ <strong>Khuyến cáo:</strong> Bạn có thể dùng Mã hoặc Email để đăng nhập. Vui lòng đổi lại mật khẩu ngay sau lần đăng nhập đầu tiên để đảm bảo bảo mật.
                        </div>
                    </div>
                    <div class="footer">
                        <p>© 2026 Hệ thống Quản trị Đại học. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(safeName, roleTitle, username, toEmail, initialPassword);

        try {
            sendHtmlEmail(toEmail, safeName, subject, htmlContent);
        } catch (Exception ex) {
            log.warn("Could not send credentials email via Brevo API or SMTP (using fallback log). To: {}, Error: {}", toEmail, ex.getMessage());
            log.info("🔑 [DEV/FALLBACK WELCOME EMAIL] Email: {} | Username: {} | Password: {}", toEmail, username, initialPassword);
        }
    }
}
