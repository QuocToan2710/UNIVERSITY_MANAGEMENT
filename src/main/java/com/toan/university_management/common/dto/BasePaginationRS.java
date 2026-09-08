package com.toan.university_management.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

/**
 * Standard Pagination Response structure for all paged list endpoints.
 *
 * @param <T> Item type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasePaginationRS<T> {

    @Builder.Default
    private List<T> items = Collections.emptyList();

    private int pageNumber;
    private int pageSize;
    private long totalCount;
    private int totalPage;
    private boolean hasNext;
    private boolean hasPrevious;

    // Getter/Setter aliases for totalElements and totalPages
    public long getTotalElements() {
        return totalCount;
    }

    public void setTotalElements(long totalElements) {
        this.totalCount = totalElements;
    }

    public int getTotalPages() {
        return totalPage;
    }

    public void setTotalPages(int totalPages) {
        this.totalPage = totalPages;
    }

    /**
     * Factory method to convert Spring Data Page<T> to BasePaginationRS<T>
     */
    public static <T> BasePaginationRS<T> from(Page<T> page) {
        if (page == null) {
            return BasePaginationRS.<T>builder()
                    .items(Collections.emptyList())
                    .pageNumber(0)
                    .pageSize(0)
                    .totalCount(0)
                    .totalPage(0)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();
        }
        return BasePaginationRS.<T>builder()
                .items(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalCount(page.getTotalElements())
                .totalPage(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * Factory method for in-memory or custom pagination
     */
    public static <T> BasePaginationRS<T> of(List<T> items, int pageNumber, int pageSize, long totalCount) {
        int validPageSize = pageSize > 0 ? pageSize : 10;
        int totalPage = (int) Math.ceil((double) totalCount / validPageSize);
        boolean hasNext = pageNumber + 1 < totalPage;
        boolean hasPrevious = pageNumber > 0;

        return BasePaginationRS.<T>builder()
                .items(items != null ? items : Collections.emptyList())
                .pageNumber(pageNumber)
                .pageSize(validPageSize)
                .totalCount(totalCount)
                .totalPage(totalPage)
                .hasNext(hasNext)
                .hasPrevious(hasPrevious)
                .build();
    }
}
