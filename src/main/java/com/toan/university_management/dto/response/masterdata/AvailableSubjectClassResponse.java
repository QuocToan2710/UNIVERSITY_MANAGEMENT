package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailableSubjectClassResponse {
    Long id;
    String subjectClassCode;
    String name;
    Long subjectId;
    String subjectCode;
    String subjectName;
    Integer credit;
    Integer attendanceCoeff;
    Integer midtermCoeff;
    Integer finalCoeff;
    Long teacherId;
    String teacherCode;
    String teacherName;
    String semester;
    String academicYear;
    int maxCapacity;
    long currentCapacity;
    boolean isEnrolled;
    Long enrollmentId;
    List<ScheduleInfo> schedules;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ScheduleInfo {
        Long id;
        int dayOfWeek;
        String shift;
        String startTime;
        String endTime;
        String room;
    }
}
