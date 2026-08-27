package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttendanceSessionRequest {

    @NotNull(message = "Lớp học phần không được để trống")
    Long subjectClassId;

    Long classScheduleId;

    Long teacherId;

    @NotNull(message = "Số thứ tự buổi học không được để trống")
    Integer sessionNumber;

    @NotNull(message = "Ngày học không được để trống")
    LocalDate sessionDate;

    @Builder.Default
    Integer lessonCount = 3;

    String room;

    String topic;

    String note;
}
