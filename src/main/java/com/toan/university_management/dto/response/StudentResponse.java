package com.toan.university_management.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentResponse {
    String id;
    String studentCode;
    String fullName;
    String email;
    String phoneNumber;
    Date dob;
    String gender;
    String address;
}
