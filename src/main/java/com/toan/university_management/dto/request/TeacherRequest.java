package com.toan.university_management.dto.request;


import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherRequest {
    String id;
    String teacherCode;
    String fullName;
    String email;
    String phoneNumber;
    String specialization;

}
