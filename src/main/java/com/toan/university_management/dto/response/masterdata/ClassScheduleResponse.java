package com.toan.university_management.dto.response.masterdata;

import com.toan.university_management.enums.WeekDay;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassScheduleResponse {
    String id;
    String courseId;
    String courseName;
    String courseCode;
    String teacherId;
    String teacherName;
    String teacherCode;
    String classGroupId;
    String classGroupName;
    String classGroupCode;
    WeekDay dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    String room;
    String semester;
    String academicYear;
    String note;
}

