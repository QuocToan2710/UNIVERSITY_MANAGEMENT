package com.toan.university_management.dto.request.masterdata;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRequest {
    String courseCode;
    String courseName;
    int credit;
    String semester;
    String teacherId;

    List<String> studentIds;
}

