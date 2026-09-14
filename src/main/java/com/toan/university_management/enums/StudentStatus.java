package com.toan.university_management.enums;

import com.toan.university_management.model.masterdata.SelectOptionResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum StudentStatus {
    ACTIVE("ACTIVE", "Đang học", "ACTIVE"),
    GRADUATED("GRADUATED", "Đã tốt nghiệp", "GRADUATED"),
    SUSPENDED("SUSPENDED", "Bảo lưu kết quả", "SUSPENDED"),
    DROPPED("DROPPED", "Thôi học", "DROPPED");

    String value;
    String label;
    String code;

    public SelectOptionResponse toSelectOption() {
        return new SelectOptionResponse(value, label, code, null);
    }
}

