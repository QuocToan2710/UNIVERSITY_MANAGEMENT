package com.toan.university_management.model.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {

    @NotBlank(message = "PASSWORD_INVALID")
    String oldPassword;

    @NotBlank(message = "PASSWORD_INVALID")
    @Size(min = 5, message = "PASSWORD_INVALID")
    String newPassword;

    String confirmPassword;
}
