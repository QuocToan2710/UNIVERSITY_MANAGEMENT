package com.toan.university_management.enums;

import lombok.Getter;

@Getter
public enum TuitionStatus {
    UNPAID("Chưa nộp"),
    PARTIALLY_PAID("Nộp một phần"),
    PAID("Đã nộp đủ"),
    OVERDUE("Quá hạn");

    private final String description;

    TuitionStatus(String description) {
        this.description = description;
    }
}