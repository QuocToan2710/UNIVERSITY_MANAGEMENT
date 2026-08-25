package com.toan.university_management.common.util;

import com.toan.university_management.constant.AppConstants;
import com.toan.university_management.dto.response.BasePaginationRS;

import java.util.Collections;
import java.util.List;

public final class PaginationUtils {

    private PaginationUtils() {
        // Prevent instantiation
    }

    /**
     * Phân trang danh sách dữ liệu trong bộ nhớ (In-Memory Pagination) cho các API tìm kiếm nâng cao & lọc.
     *
     * @param items       Danh sách toàn bộ phần tử sau khi filter
     * @param pageNumber  Số trang (0-indexed)
     * @param pageSize    Số phần tử trên 1 trang
     * @param <T>         Kiểu dữ liệu phần tử
     * @return BasePaginationRS chứa danh sách trang, tổng số bản ghi và tổng số trang
     */
    public static <T> BasePaginationRS<T> paginateList(List<T> items, int pageNumber, int pageSize) {
        if (items == null || items.isEmpty()) {
            return BasePaginationRS.<T>builder()
                    .items(Collections.emptyList())
                    .totalCount(0)
                    .totalPage(0)
                    .build();
        }

        int validPage = Math.max(0, pageNumber);
        int validSize = pageSize > 0 ? pageSize : AppConstants.DEFAULT_PAGE_SIZE;

        long totalCount = items.size();
        int start = validPage * validSize;
        List<T> pageList = (start < totalCount)
                ? items.subList(start, Math.min(start + validSize, (int) totalCount))
                : Collections.emptyList();

        int totalPage = (int) (totalCount / validSize);
        if (totalCount % validSize != 0) {
            totalPage++;
        }

        return BasePaginationRS.<T>builder()
                .items(pageList)
                .totalCount(totalCount)
                .totalPage(totalPage)
                .build();
    }
}
