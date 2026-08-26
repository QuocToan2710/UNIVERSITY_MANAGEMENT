package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuitionItemResponse {
    Long subjectClassId;
    String subjectClassCode;
    String subjectClassName;
    String subjectCode;
    String subjectName;
    Integer credit;
    Long pricePerCredit;
    Long totalAmount;
    String enrolledAt;
    String status;
}