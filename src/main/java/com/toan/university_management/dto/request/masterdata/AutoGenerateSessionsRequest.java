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
public class AutoGenerateSessionsRequest {

    @NotNull(message = "Lớp học phần không được để trống")
    Long subjectClassId;

    @Builder.Default
    Integer totalSessions = 15;

    LocalDate startDate;
}
