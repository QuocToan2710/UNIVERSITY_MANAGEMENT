package com.toan.university_management.dto.request.masterdata;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.toan.university_management.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentRequest {
    Long id;
    String enrollmentCode;
    String name;
    Long studentId;
    @JsonAlias({"courseClassId"})
    Long subjectClassId;
    Double midtermScore;
    Double finalScore;
    EnrollmentStatus status;
}
