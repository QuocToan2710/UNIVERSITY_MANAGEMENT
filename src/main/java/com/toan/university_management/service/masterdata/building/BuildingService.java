package com.toan.university_management.service.masterdata.building;

import com.toan.university_management.model.masterdata.BuildingRequest;
import com.toan.university_management.model.masterdata.BuildingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import com.toan.university_management.model.masterdata.BuildingSearchPaginationRQ;
import com.toan.university_management.common.dto.BasePaginationRS;

public interface BuildingService {
    BuildingResponse createBuilding(BuildingRequest request);
    BuildingResponse getBuildingById(Long id);
    List<BuildingResponse> getAllBuildings();
    Page<BuildingResponse> getAllBuildings(Pageable pageable);
    BuildingResponse updateBuilding(Long id, BuildingRequest request);
    void deleteBuilding(Long id);
    BasePaginationRS<BuildingResponse> search(BuildingSearchPaginationRQ search);
    List<BuildingResponse> export(BuildingSearchPaginationRQ search);
}
