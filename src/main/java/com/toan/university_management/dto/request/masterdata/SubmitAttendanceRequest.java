package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmitAttendanceRequest {

    @NotEmpty(message = "Danh sách điểm danh không được để trống")
    List<AttendanceRecordItemRequest> records;

    String topic;

    String note;
}
