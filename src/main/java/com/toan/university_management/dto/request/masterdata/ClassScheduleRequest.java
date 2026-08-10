package com.toan.university_management.dto.request.masterdata;

import com.toan.university_management.enums.WeekDay;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassScheduleRequest {

    @NotBlank(message = "Course ID is required")
    String courseId;

    @NotBlank(message = "Teacher ID is required")
    String teacherId;

    @NotBlank(message = "Class group ID is required")
    String classGroupId;

    @NotNull(message = "Day of week is required")
    WeekDay dayOfWeek;

    @NotNull(message = "Start time is required")
    LocalTime startTime;

    @NotNull(message = "End time is required")
    LocalTime endTime;

    String room;
    String semester;        // VD: HK1, HK2
    String academicYear;    // VD: 2025-2026
    String note;
}

