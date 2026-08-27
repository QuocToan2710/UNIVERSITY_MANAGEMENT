package com.toan.university_management.dto.response.masterdata;

import com.toan.university_management.enums.AttendanceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceRecordResponse {
    Long id;
    Long sessionId;
    Integer sessionNumber;
    String sessionDate;
    Long enrollmentId;
    Long studentId;
    String studentCode;
    String studentName;
    String classGroupName;
    AttendanceStatus status;
    Integer lateMinutes;
    String note;
    LocalDateTime checkedAt;
}
