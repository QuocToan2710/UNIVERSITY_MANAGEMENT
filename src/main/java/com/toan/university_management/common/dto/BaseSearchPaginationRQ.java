package com.toan.university_management.common.dto;

import com.toan.university_management.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseSearchPaginationRQ {

    private int pageNumber = AppConstants.DEFAULT_PAGE_NUMBER;
    private int pageSize = AppConstants.DEFAULT_PAGE_SIZE;
    private String keyword;
    private String sortBy;
    private String sortDirection = "ASC";
}
