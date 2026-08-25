package com.toan.university_management.dto.response.masterdata;

import com.toan.university_management.enums.GradeStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectClassGradeSummaryResponse {
    Long subjectClassId;
    String subjectClassCode;
    String subjectClassName;
    String semester;
    String academicYear;
    int maxCapacity;

    Long subjectId;
    String subjectCode;
    String subjectName;
    int credit;

    int attendanceCoeff;
    int midtermCoeff;
    int finalCoeff;

    Long teacherId;
    String teacherCode;
    String teacherName;

    GradeStatus gradeStatus;
    int totalStudents;
    int gradedStudents;
    int passedCount;
    int failedCount;
    Double averageScore;

    Map<String, Integer> gradeDistribution; // e.g. "A": 5, "B+": 10, "B": 15, "C": 5, "F": 2

    List<EnrollmentResponse> studentGrades;
}
