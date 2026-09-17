package com.toan.university_management.model.masterdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.toan.university_management.constant.AppConstants;
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
    @JsonFormat(pattern = AppConstants.DATE_FORMAT)
    LocalDate examDate;
    @JsonFormat(pattern = "HH:mm")
    LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
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
