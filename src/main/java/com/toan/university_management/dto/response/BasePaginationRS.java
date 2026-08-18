package com.toan.university_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasePaginationRS<T> {
    private List<T> items;
    private long totalCount;
    private int totalPage;
}
