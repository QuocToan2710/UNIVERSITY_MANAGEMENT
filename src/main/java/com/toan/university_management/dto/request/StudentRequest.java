package com.toan.university_management.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentRequest {

    String studentCode;
    String fullName;
    String email;
    String phoneNumber;
    LocalDate dob;
    String gender;
    String address;
}
