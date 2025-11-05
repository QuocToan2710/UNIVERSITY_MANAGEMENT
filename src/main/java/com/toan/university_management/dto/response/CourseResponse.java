package com.toan.university_management.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    String id;
    String courseCode;
    String courseName;
    int credit;
    String semester;

    String teacherId;
    String teacherCode;
    String teacherName;
    String teacherEmail;
    String teacherPhone;
    String teacherSpecialization;

    List<StudentSummary> students;
}
