package com.toan.university_management.service.email;

public interface EmailService {
    void sendOtpEmail(String toEmail, String recipientName, String otpCode);
    void sendAccountCreatedEmail(String toEmail, String recipientName, String username, String initialPassword, String roleName);
}
