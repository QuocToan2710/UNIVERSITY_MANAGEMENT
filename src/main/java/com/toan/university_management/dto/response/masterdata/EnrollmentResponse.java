package com.toan.university_management.dto.response.masterdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.toan.university_management.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentResponse {
    Long id;
    String enrollmentCode;
    String name;
    Long studentId;
    String studentCode;
    String studentName;
    Long subjectClassId;
    String subjectClassCode;
    String subjectClassName;
    Double attendanceScore;
    Double midtermScore;
    Double finalScore;
    Double totalScore;
    String letterGrade;
    Double gradePoint4;
    com.toan.university_management.enums.GradeStatus gradeStatus;
    String note;
    boolean isAppealed;
    EnrollmentStatus status;
    LocalDateTime enrolledAt;
    String subjectName;
    String subjectCode;
    Integer credit;
    String semester;
    String academicYear;

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
}
