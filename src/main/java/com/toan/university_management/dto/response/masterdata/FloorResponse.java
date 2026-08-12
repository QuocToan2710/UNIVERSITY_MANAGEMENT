package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorResponse {
    Long id;
    String floorCode;
    String name;
    Long buildingId;
    String buildingName;
    Integer floorNumber;
    String status;
    String description;
}
