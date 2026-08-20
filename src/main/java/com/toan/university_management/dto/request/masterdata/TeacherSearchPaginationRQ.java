package com.toan.university_management.dto.request.masterdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSearchPaginationRQ {
    @Builder.Default
    private int pageNumber = 0;
    @Builder.Default
    private int pageSize = 10;
    private String keyword;
    private String teacherCode;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String degree;
    private Long departmentId;
    private Long provinceId;
    private Long districtId;
    private Long wardId;
}
