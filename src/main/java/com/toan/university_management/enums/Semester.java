package com.toan.university_management.enums;

import com.toan.university_management.dto.response.masterdata.SelectOptionResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Semester {
    SEMESTER_1("1", "Học kỳ 1", "HK1"),
    SEMESTER_2("2", "Học kỳ 2", "HK2"),
    SEMESTER_3("3", "Học kỳ Hè", "HK3");

    String value;
    String label;
    String code;

    public SelectOptionResponse toSelectOption() {
        return new SelectOptionResponse(value, label, code, null);
    }
}
