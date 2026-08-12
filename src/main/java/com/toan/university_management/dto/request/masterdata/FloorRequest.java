package com.toan.university_management.dto.request.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FloorRequest {
    Long id;
    String floorCode;
    String name;
    Long buildingId;
    Integer floorNumber;
    String status;
    String description;
}
