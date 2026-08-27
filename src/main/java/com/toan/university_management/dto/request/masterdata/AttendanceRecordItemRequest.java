package com.toan.university_management.dto.request.masterdata;

import com.toan.university_management.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceRecordItemRequest {

    @NotNull(message = "Sinh viên không được để trống")
    Long studentId;

    Long enrollmentId;

    @NotNull(message = "Trạng thái điểm danh không được để trống")
    AttendanceStatus status;

    @Builder.Default
    Integer lateMinutes = 0;

    String note;
}
