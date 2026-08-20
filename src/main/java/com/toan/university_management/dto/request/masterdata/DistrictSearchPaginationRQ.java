package com.toan.university_management.dto.request.masterdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistrictSearchPaginationRQ {
    @Builder.Default
    private int pageNumber = 0;
    @Builder.Default
    private int pageSize = 10;
    private String keyword;
    private String districtCode;
    private String districtName;
    private String districtType;
    private Long provinceId;
}
