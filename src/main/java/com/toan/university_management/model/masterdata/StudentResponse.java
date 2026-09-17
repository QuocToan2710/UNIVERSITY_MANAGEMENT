package com.toan.university_management.model.masterdata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toan.university_management.constant.AppConstants;
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
    @JsonFormat(pattern = AppConstants.DATE_FORMAT, timezone = "Asia/Ho_Chi_Minh")
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
    Long userId;
}
