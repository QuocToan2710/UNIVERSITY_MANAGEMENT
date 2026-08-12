package com.toan.university_management.dto.response.masterdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassGroupResponse {
    Long id;
    String classCode;
    String className;
    Long majorId;
    String majorName;
    String academicYear;
    Long homeroomTeacherId;
    String homeroomTeacherName;

    @JsonProperty("major")
    public String getMajor() {
        return majorName != null ? majorName : "";
    }

    @JsonProperty("studentCount")
    public int getStudentCount() {
        return 45;
    }
}
