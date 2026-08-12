package com.toan.university_management.dto.request.masterdata;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamScheduleRequest {
    Long id;
    String examCode;
    String name;
    @JsonAlias({"courseClassId"})
    Long subjectClassId;
    Long subjectId;
    LocalDate examDate;
    LocalTime startTime;
    LocalTime endTime;
    String room;
    String examFormat;
    Long proctorId;
    String proctorName;
    String semester;
    String academicYear;
}
