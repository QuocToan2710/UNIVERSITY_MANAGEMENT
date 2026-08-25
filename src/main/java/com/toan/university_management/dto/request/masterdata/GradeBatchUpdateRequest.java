package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GradeBatchUpdateRequest {
    @NotEmpty(message = "Grade items cannot be empty")
    @Valid
    List<GradeItemRequest> items;
}
