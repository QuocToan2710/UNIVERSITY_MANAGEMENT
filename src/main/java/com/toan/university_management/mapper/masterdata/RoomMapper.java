package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.RoomRequest;
import com.toan.university_management.dto.response.masterdata.RoomResponse;
import com.toan.university_management.entity.masterdata.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(target = "deleted", ignore = true)
    Room toRoom(RoomRequest request);
    RoomResponse toRoomResponse(Room room);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateRoom(@MappingTarget Room room, RoomRequest request);
}
