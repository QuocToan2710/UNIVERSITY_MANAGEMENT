package com.toan.university_management.dto.request.masterdata;

import com.toan.university_management.common.dto.BaseSearchPaginationRQ;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSearchPaginationRQ extends BaseSearchPaginationRQ {
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
