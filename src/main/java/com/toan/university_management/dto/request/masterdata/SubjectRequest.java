package com.toan.university_management.dto.request.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectRequest {
    Long id;
    String subjectCode;
    String name;
    int credit;
    @Builder.Default
    int attendanceCoeff = 1;
    @Builder.Default
    int midtermCoeff = 3;
    @Builder.Default
    int finalCoeff = 6;
    Long departmentId;
    String description;
}
