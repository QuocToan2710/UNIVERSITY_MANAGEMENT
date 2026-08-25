package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SemesterTranscriptResponse {
    String semester;
    String academicYear;
    Double semesterGpa4;
    Double semesterGpa10;
    int semesterCredits;
    int semesterEarnedCredits;
    List<EnrollmentResponse> courses;
}
