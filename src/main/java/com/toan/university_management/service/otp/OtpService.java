package com.toan.university_management.service.otp;

public interface OtpService {
    String generateAndStoreOtp(String email);
    boolean verifyOtp(String email, String otp);
    void clearOtp(String email);
}
