package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchEnrollmentResultResponse {
    int totalRequested;
    int successCount;
    int failedCount;
    List<String> successStudentCodes;
    List<String> failedReasons;
}
