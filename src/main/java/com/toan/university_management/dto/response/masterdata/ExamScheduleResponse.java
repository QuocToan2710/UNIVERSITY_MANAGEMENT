package com.toan.university_management.dto.response.masterdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamScheduleResponse {
    Long id;
    String examCode;
    String name;
    Long subjectClassId;
    Long subjectId;
    String subjectName;
    String subjectCode;
    LocalDate examDate;
    LocalTime startTime;
    LocalTime endTime;
    String room;
    String examFormat;
    Long proctorId;
    String proctorName;
    String semester;
    String academicYear;

    @JsonProperty("courseClassId")
    public Long getCourseClassId() {
        return subjectClassId;
    }
}
