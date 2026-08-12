package com.toan.university_management.dto.request.masterdata;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.toan.university_management.enums.WeekDay;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassScheduleRequest {
    Long id;
    String scheduleCode;
    String name;
    @JsonAlias({"courseClassId"})
    Long subjectClassId;
    Long teacherId;
    WeekDay dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    String room;
    String semester;
    String academicYear;
    String note;
}
