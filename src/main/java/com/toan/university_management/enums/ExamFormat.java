package com.toan.university_management.enums;

import com.toan.university_management.dto.response.masterdata.SelectOptionResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ExamFormat {
    WRITTEN("Tự luận", "Tự luận", "WRITTEN"),
    MULTIPLE_CHOICE("Trắc nghiệm", "Trắc nghiệm", "MULTIPLE_CHOICE"),
    PRACTICAL("Thực hành", "Thực hành", "PRACTICAL"),
    PROJECT("Báo cáo đồ án", "Báo cáo đồ án", "PROJECT");

    String value;
    String label;
    String code;

    public SelectOptionResponse toSelectOption() {
        return new SelectOptionResponse(value, label, code, null);
    }
}
