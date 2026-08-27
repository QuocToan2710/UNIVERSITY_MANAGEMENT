package com.toan.university_management.dto.response.masterdata;

import com.toan.university_management.enums.AttendanceSessionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceSessionResponse {
    Long id;
    String sessionCode;
    String name;
    Long subjectClassId;
    String subjectClassCode;
    String subjectClassName;
    String subjectName;
    Long classScheduleId;
    Long teacherId;
    String teacherName;
    Integer sessionNumber;
    LocalDate sessionDate;
    Integer lessonCount;
    String room;
    String topic;
    String note;
    AttendanceSessionStatus status;
    Integer totalStudents;
    Integer presentStudents;
    Integer absentStudents;
    Integer lateStudents;
    LocalDateTime createdAt;
    String createdBy;
}
