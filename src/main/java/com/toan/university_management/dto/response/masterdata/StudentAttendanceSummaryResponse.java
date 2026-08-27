package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentAttendanceSummaryResponse {
    Long enrollmentId;
    Long subjectClassId;
    String subjectClassCode;
    String subjectClassName;
    String subjectName;
    Integer credits;
    String teacherName;
    Integer totalPlannedSessions;
    Integer completedSessions;
    Integer attendedSessions;
    Integer excusedAbsentSessions;
    Integer unexcusedAbsentSessions;
    Integer lateSessions;
    Double absenceRate;
    Double attendanceScore;
    Boolean isBannedFromExam;
    String examStatus; // ELIGIBLE, AT_RISK, BANNED
    List<AttendanceRecordResponse> records;
}
