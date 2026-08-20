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
public class DistrictRequest {
    Long id;

    @NotBlank(message = "District code is required")
    String districtCode;

    @NotBlank(message = "District name is required")
    String districtName;

    String districtType; // "Quận", "Huyện", "Thị xã", "Thành phố thuộc tỉnh"

    @NotNull(message = "Province ID is required")
    Long provinceId;
}
