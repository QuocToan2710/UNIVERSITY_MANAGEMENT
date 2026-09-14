package com.toan.university_management.enums;

import com.toan.university_management.model.masterdata.SelectOptionResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum AcademicDegree {
    DOCTOR("Tiến sĩ", "Tiến sĩ", "TS"),
    MASTER("Thạc sĩ", "Thạc sĩ", "ThS"),
    ASSOCIATE_PROFESSOR("Phó Giáo sư", "Phó Giáo sư (PGS)", "PGS"),
    PROFESSOR("Giáo sư", "Giáo sư (GS)", "GS"),
    BACHELOR("Cử nhân", "Cử nhân (CN)", "CN"),
    ENGINEER("Kỹ sư", "Kỹ sư (KS)", "KS");

    String value;
    String label;
    String code;

    public SelectOptionResponse toSelectOption() {
        return new SelectOptionResponse(value, label, code, null);
    }
}
