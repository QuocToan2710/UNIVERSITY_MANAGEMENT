package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassGroupEnrollmentRequest {
    @NotNull(message = "Lớp học phần không được để trống")
    Long subjectClassId;

    @NotNull(message = "Lớp sinh hoạt không được để trống")
    Long classGroupId;
}
