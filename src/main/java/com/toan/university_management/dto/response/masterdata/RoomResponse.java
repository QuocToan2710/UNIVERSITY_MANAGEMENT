package com.toan.university_management.dto.response.masterdata;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomResponse {
    Long id;
    String roomCode;
    String name;
    Long buildingId;
    String building;
    Long floorId;
    String floor;
    Integer capacity;
    String roomType;
    String status;
    String description;
}
