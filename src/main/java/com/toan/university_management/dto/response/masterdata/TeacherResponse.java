package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherResponse {
    Long id;
    String teacherCode;
    String fullName;
    String email;
    String phoneNumber;
    String degree;
    String address;
    Long provinceId;
    String provinceName;
    Long districtId;
    String districtName;
    Long wardId;
    String wardName;
    String specificAddress;
    String fullAddress;
    Long departmentId;
    String departmentName;
    Long userId;
}
