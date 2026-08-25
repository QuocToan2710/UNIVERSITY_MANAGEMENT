package com.toan.university_management.dto.response.masterdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectResponse {
    Long id;
    String subjectCode;
    String name;
    int credit;
    int attendanceCoeff;
    int midtermCoeff;
    int finalCoeff;
    String description;

    @JsonProperty("courseCode")
    public String getCourseCode() {
        return subjectCode;
    }

    @JsonProperty("courseName")
    public String getCourseName() {
        return name;
    }

    @JsonProperty("semester")
    public String getSemester() {
        return "HK1-2025";
    }
}
