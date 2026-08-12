package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.BuildingRequest;
import com.toan.university_management.dto.response.masterdata.BuildingResponse;
import com.toan.university_management.entity.masterdata.Building;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BuildingMapper {
    Building toBuilding(BuildingRequest request);
    BuildingResponse toBuildingResponse(Building building);
    void updateBuilding(@MappingTarget Building building, BuildingRequest request);
}
