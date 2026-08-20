package com.toan.university_management.dto.response.masterdata;

import com.toan.university_management.enums.StudentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentResponse {
    Long id;
    String studentCode;
    String fullName;
    String email;
    String phoneNumber;
    Date dob;
    String gender;
    String address;
    Long provinceId;
    String provinceName;
    Long districtId;
    String districtName;
    Long wardId;
    String wardName;
    String specificAddress;
    String fullAddress;
    String enrollmentYear;
    StudentStatus status;
    Long classGroupId;
    String classCode;
    String classGroupName;
    Long majorId;
    String majorName;
    String userId;
}
