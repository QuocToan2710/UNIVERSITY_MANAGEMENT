package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchEnrollmentRequest {
    @NotNull(message = "Lớp học phần không được để trống")
    Long subjectClassId;

    @NotEmpty(message = "Danh sách sinh viên không được rỗng")
    List<Long> studentIds;
}
