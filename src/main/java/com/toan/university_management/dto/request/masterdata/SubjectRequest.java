package com.toan.university_management.dto.request.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectRequest {
    Long id;
    String subjectCode;
    String name;
    int credit;
    Long departmentId;
    String description;
}
