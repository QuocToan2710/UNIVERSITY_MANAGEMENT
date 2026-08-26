package com.toan.university_management.dto.response.masterdata;

import com.toan.university_management.enums.TuitionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentTuitionSummaryResponse {
    Long tuitionFeeId;
    Long studentId;
    String studentCode;
    String fullName;
    String email;
    String phone;
    Long classGroupId;
    String classGroupCode;
    String classGroupName;
    String majorName;
    String semester;
    String academicYear;
    Integer totalCredits;
    Long pricePerCredit;
    Long totalAmount;
    Long discountAmount;
    Long paidAmount;
    Long balanceAmount;
    LocalDate dueDate;
    TuitionStatus status;
    String statusDescription;
    String notes;
    List<TuitionItemResponse> items;
}