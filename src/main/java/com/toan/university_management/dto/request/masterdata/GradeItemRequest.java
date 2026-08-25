package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradeItemRequest {
    @NotNull(message = "Enrollment ID is required")
    Long enrollmentId;

    @DecimalMin(value = "0.0", message = "Attendance score must be >= 0.0")
    @DecimalMax(value = "10.0", message = "Attendance score must be <= 10.0")
    Double attendanceScore;

    @DecimalMin(value = "0.0", message = "Midterm score must be >= 0.0")
    @DecimalMax(value = "10.0", message = "Midterm score must be <= 10.0")
    Double midtermScore;

    @DecimalMin(value = "0.0", message = "Final score must be >= 0.0")
    @DecimalMax(value = "10.0", message = "Final score must be <= 10.0")
    Double finalScore;

    String note;
}
