package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentTranscriptResponse {
    Long studentId;
    String studentCode;
    String fullName;
    String email;
    String className;
    String majorName;
    String academicStatus;

    Double cumulativeCpa4;
    Double cumulativeGpa10;
    int totalRegisteredCredits;
    int totalEarnedCredits;
    String academicRank; // Xuất sắc, Giỏi, Khá, Trung bình, Yếu

    List<SemesterTranscriptResponse> semesters;
}
