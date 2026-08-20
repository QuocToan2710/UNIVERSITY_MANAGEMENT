package com.toan.university_management.dto.request.masterdata;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProvinceRequest {
    Long id;

    @NotBlank(message = "Province code is required")
    String provinceCode;

    @NotBlank(message = "Province name is required")
    String provinceName;

    String provinceType; // "Thành phố Trung ương", "Tỉnh"
}
