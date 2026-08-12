package com.toan.university_management.dto.response.masterdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectClassResponse {
    Long id;
    String subjectClassCode;
    String name;
    Long subjectId;
    String subjectCode;
    String subjectName;
    Long teacherId;
    String teacherName;
    String teacherCode;
    String semester;
    String academicYear;
    int maxCapacity;

    @JsonProperty("courseClassCode")
    public String getCourseClassCode() {
        return subjectClassCode;
    }

    @JsonProperty("courseCode")
    public String getCourseCode() {
        return subjectClassCode;
    }

    @JsonProperty("courseName")
    public String getCourseName() {
        return name;
    }

    @JsonProperty("credit")
    public int getCredit() {
        return 3;
    }
}
