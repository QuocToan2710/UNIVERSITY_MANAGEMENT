package com.toan.university_management.dto.request.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingRequest {
    Long id;
    String buildingCode;
    String name;
    Integer totalFloors;
    String status;
    String description;
}
