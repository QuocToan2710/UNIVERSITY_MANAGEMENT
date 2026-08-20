package com.toan.university_management.dto.request.masterdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSearchPaginationRQ {
    @Builder.Default
    private int pageNumber = 0;
    @Builder.Default
    private int pageSize = 10;
    private String keyword;
    private String studentCode;
    private String fullName;
    private String email;
    private Long majorId;
    private Long classGroupId;
    private Long provinceId;
    private Long districtId;
    private Long wardId;
}
