package com.toan.university_management.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.toan.university_management.constant.AppConstants;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard API Response envelope wrapper for all REST endpoints in the system.
 *
 * @param <T> Payload data type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Builder.Default
    int code = 1000;

    String message;

    T result;

    String path;

    @JsonFormat(pattern = AppConstants.DATE_TIME_FORMAT)
    LocalDateTime timestamp;

    String traceId;

    Map<String, String> fieldErrors;

    // Helper method to access result as data
    public T getData() {
        return result;
    }

    public void setData(T data) {
        this.result = data;
    }
}
