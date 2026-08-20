package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DistrictResponse {
    Long id;
    String districtCode;
    String districtName;
    String districtType;
    Long provinceId;
    String provinceName;
    Integer wardCount;
}
