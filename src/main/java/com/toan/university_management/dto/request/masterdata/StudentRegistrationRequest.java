package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentRegistrationRequest {
    @NotNull(message = "Lớp học phần không được để trống")
    Long subjectClassId;

    Long studentId;
}
