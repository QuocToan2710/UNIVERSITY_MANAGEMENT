package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassGroupRequest {

    @NotBlank(message = "Class code is required")
    String classCode;

    @NotBlank(message = "Class name is required")
    String className;

    String major;
    String academicYear;
    String homeroomTeacherId;   // Giáo viên chủ nhiệm (optional)
}

