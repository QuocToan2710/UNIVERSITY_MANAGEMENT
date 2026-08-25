package com.toan.university_management.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {
    @NotBlank(message = "Email/Username is required")
    String email;

    @NotBlank(message = "OTP is required")
    String otp;

    @NotBlank(message = "New password is required")
    @Size(min = 5, message = "PASSWORD_INVALID")
    String newPassword;
}
