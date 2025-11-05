package com.toan.university_management.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherResponse {
    String id;
    String teacherCode;
    String fullName;
    String email;
    String phoneNumber;
    String specialization;
}
