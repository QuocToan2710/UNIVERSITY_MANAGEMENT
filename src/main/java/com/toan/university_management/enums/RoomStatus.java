package com.toan.university_management.enums;

import com.toan.university_management.dto.response.masterdata.SelectOptionResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum RoomStatus {
    ACTIVE("ACTIVE", "Hoạt động", "ACTIVE"),
    MAINTENANCE("MAINTENANCE", "Đang bảo trì", "MAINTENANCE"),
    INACTIVE("INACTIVE", "Tạm khóa", "INACTIVE");

    String value;
    String label;
    String code;

    public SelectOptionResponse toSelectOption() {
        return new SelectOptionResponse(value, label, code, null);
    }
}
