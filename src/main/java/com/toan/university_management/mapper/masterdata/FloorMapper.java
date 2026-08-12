package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.FloorRequest;
import com.toan.university_management.dto.response.masterdata.FloorResponse;
import com.toan.university_management.entity.masterdata.Floor;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FloorMapper {
    Floor toFloor(FloorRequest request);
    FloorResponse toFloorResponse(Floor floor);
    void updateFloor(@MappingTarget Floor floor, FloorRequest request);
}
