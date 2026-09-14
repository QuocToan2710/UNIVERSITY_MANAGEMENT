package com.toan.university_management.model.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassGroupRequest {
    Long id;
    String classCode;
    String className;
    Long majorId;
    String academicYear;
    Long homeroomTeacherId;
    Integer maxStudents;
}
