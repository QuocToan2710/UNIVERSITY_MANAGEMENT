package com.toan.university_management.dto.request.masterdata;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectClassRequest {
    Long id;
    @JsonAlias({"courseClassCode", "courseCode"})
    String subjectClassCode;
    @JsonAlias({"courseName", "className"})
    String name;
    Long subjectId;
    Long teacherId;
    String semester;
    String academicYear;
    int maxCapacity;
}
