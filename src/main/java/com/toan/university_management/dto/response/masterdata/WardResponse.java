package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WardResponse {
    Long id;
    String wardCode;
    String wardName;
    String wardType;
    Long districtId;
    String districtName;
    Long provinceId;
    String provinceName;
}
