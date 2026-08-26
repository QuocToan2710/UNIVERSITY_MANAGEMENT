package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TuitionDashboardSummaryResponse {
    String semester;
    String academicYear;
    long totalStudents;
    long totalCreditsEnrolled;
    long totalTuitionExpected;
    long totalTuitionDiscount;
    long totalTuitionCollected;
    long totalTuitionDebt;
    double collectionRatePercent;
    long paidCount;
    long partiallyPaidCount;
    long unpaidCount;
    long overdueCount;
}