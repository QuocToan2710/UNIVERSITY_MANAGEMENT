package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.RoomRequest;
import com.toan.university_management.dto.response.masterdata.RoomResponse;
import com.toan.university_management.entity.masterdata.Room;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toRoom(RoomRequest request);
    RoomResponse toRoomResponse(Room room);
    void updateRoom(@MappingTarget Room room, RoomRequest request);
}
