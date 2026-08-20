package com.toan.university_management.dto.request.masterdata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WardSearchPaginationRQ {
    @Builder.Default
    private int pageNumber = 0;
    @Builder.Default
    private int pageSize = 10;
    private String keyword;
    private String wardCode;
    private String wardName;
    private String wardType;
    private Long districtId;
    private Long provinceId;
}
