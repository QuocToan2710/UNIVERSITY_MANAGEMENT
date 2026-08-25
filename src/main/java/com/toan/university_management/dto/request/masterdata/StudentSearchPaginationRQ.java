package com.toan.university_management.dto.request.masterdata;

import com.toan.university_management.common.dto.BaseSearchPaginationRQ;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSearchPaginationRQ extends BaseSearchPaginationRQ {
    private String studentCode;
    private String fullName;
    private String email;
    private Long majorId;
    private Long classGroupId;
    private Long provinceId;
    private Long districtId;
    private Long wardId;
}
