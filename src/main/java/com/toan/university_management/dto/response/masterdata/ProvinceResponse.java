package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProvinceResponse {
    Long id;
    String provinceCode;
    String provinceName;
    String provinceType;
    Integer districtCount;
}
