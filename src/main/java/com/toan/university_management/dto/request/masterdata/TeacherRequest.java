package com.toan.university_management.dto.request.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeacherRequest {
    Long id;
    String teacherCode;
    String fullName;
    String email;
    String phoneNumber;
    String degree;
    String address;
    Long provinceId;
    Long districtId;
    Long wardId;
    String specificAddress;
    Long departmentId;
    Long userId;
}
