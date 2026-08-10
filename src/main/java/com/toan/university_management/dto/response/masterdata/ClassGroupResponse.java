package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassGroupResponse {
    String id;
    String classCode;
    String className;
    String major;
    String academicYear;
    String homeroomTeacherId;
    String homeroomTeacherName;
    int studentCount;
}

