package com.toan.university_management.dto.request.masterdata;

import com.toan.university_management.enums.StudentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentRequest {
    Long id;
    String studentCode;
    String fullName;
    String email;
    String phoneNumber;
    Date dob;
    String gender;
    String address;
    Long provinceId;
    Long districtId;
    Long wardId;
    String specificAddress;
    String enrollmentYear;
    StudentStatus status;
    Long classGroupId;
    Long majorId;
    Long userId;
}
