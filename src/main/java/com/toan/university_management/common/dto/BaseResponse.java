package com.toan.university_management.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toan.university_management.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseResponse {

    private Long id;

    @JsonFormat(pattern = AppConstants.DATE_TIME_FORMAT)
    private LocalDateTime createdAt;

    private String createdBy;

    @JsonFormat(pattern = AppConstants.DATE_TIME_FORMAT)
    private LocalDateTime updatedAt;

    private String updatedBy;
}
