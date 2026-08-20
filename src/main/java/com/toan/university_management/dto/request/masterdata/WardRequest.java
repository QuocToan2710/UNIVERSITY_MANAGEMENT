package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WardRequest {
    Long id;

    @NotBlank(message = "Ward code is required")
    String wardCode;

    @NotBlank(message = "Ward name is required")
    String wardName;

    String wardType; // "Phường", "Xã", "Thị trấn"

    @NotNull(message = "District ID is required")
    Long districtId;
}
