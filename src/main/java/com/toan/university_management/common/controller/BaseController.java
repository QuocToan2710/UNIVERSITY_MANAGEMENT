package com.toan.university_management.common.controller;

import com.toan.university_management.common.dto.ApiResponse;
import com.toan.university_management.common.dto.BasePaginationRS;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * BaseController provides standardized response helpers and security context access
 * for all REST controllers in the system.
 */
public abstract class BaseController {

    /**
     * Wrap data into successful ApiResponse with default message
     */
    protected <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(1000)
                .message("Success")
                .result(data)
                .build();
    }

    /**
     * Wrap data into successful ApiResponse with custom message
     */
    protected <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .code(1000)
                .message(message)
                .result(data)
                .build();
    }

    /**
     * Wrap Spring Data Page<T> into standard BasePaginationRS<T> ApiResponse
     */
    protected <T> ApiResponse<BasePaginationRS<T>> paginate(Page<T> page) {
        return ApiResponse.<BasePaginationRS<T>>builder()
                .code(1000)
                .message("Success")
                .result(BasePaginationRS.from(page))
                .build();
    }

    /**
     * Wrap BasePaginationRS<T> into standard ApiResponse
     */
    protected <T> ApiResponse<BasePaginationRS<T>> paginate(BasePaginationRS<T> paginationRS) {
        return ApiResponse.<BasePaginationRS<T>>builder()
                .code(1000)
                .message("Success")
                .result(paginationRS)
                .build();
    }

    /**
     * Wrap in-memory list into standard BasePaginationRS<T> ApiResponse
     */
    protected <T> ApiResponse<BasePaginationRS<T>> paginate(List<T> items, int pageNumber, int pageSize, long totalElements) {
        return ApiResponse.<BasePaginationRS<T>>builder()
                .code(1000)
                .message("Success")
                .result(BasePaginationRS.of(items, pageNumber, pageSize, totalElements))
                .build();
    }

    /**
     * Retrieve the currently authenticated username from SecurityContext
     */
    protected String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
    }
}
