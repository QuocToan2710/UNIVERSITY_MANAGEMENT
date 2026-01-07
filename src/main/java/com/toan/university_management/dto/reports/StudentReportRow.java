package com.toan.university_management.dto.reports;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentReportRow {
    String studentName;
    String className;
    Integer score;
}
