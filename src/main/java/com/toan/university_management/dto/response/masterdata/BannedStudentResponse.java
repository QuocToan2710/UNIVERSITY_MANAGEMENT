package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannedStudentResponse {
    Long enrollmentId;
    Long studentId;
    String studentCode;
    String studentName;
    String studentEmail;
    String classGroupName;
    Long subjectClassId;
    String subjectClassCode;
    String subjectClassName;
    String subjectName;
    String semester;
    String academicYear;
    Integer totalSessions;
    Integer absentSessions;
    Double absenceRate;
    Double attendanceScore;
    String reason;
}
