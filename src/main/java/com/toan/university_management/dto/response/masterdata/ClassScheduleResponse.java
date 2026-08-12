package com.toan.university_management.dto.response.masterdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toan.university_management.enums.WeekDay;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassScheduleResponse {
    Long id;
    String scheduleCode;
    String name;
    Long subjectClassId;
    String subjectClassCode;
    String subjectClassName;
    Long teacherId;
    String teacherCode;
    String teacherName;
    WeekDay dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    String room;
    String semester;
    String academicYear;
    String note;

    @JsonProperty("courseClassId")
    public Long getCourseClassId() {
        return subjectClassId;
    }

    @JsonProperty("courseClassCode")
    public String getCourseClassCode() {
        return subjectClassCode;
    }

    @JsonProperty("courseClassName")
    public String getCourseClassName() {
        return subjectClassName;
    }

    @JsonProperty("startPeriod")
    public int getStartPeriod() {
        return 1;
    }

    @JsonProperty("endPeriod")
    public int getEndPeriod() {
        return 3;
    }
}
