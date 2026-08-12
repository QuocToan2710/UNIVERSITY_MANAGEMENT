package com.toan.university_management.service.masterdata.building;

import com.toan.university_management.dto.request.masterdata.BuildingRequest;
import com.toan.university_management.dto.response.masterdata.BuildingResponse;
import com.toan.university_management.entity.masterdata.Building;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.BuildingMapper;
import com.toan.university_management.repository.masterdata.BuildingRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class BuildingServiceImpl implements BuildingService {
    BuildingRepository buildingRepository;
    BuildingMapper buildingMapper;

    @Override
    public BuildingResponse createBuilding(BuildingRequest request) {
        if (buildingRepository.existsByBuildingCodeAndDeletedFalse(request.getBuildingCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Building building = buildingMapper.toBuilding(request);
        if (building.getStatus() == null || building.getStatus().isBlank()) {
            building.setStatus("ACTIVE");
        }
        building = buildingRepository.save(building);
        return buildingMapper.toBuildingResponse(building);
    }

    @Override
    public BuildingResponse getBuildingById(Long id) {
        Building building = buildingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return buildingMapper.toBuildingResponse(building);
    }

    @Override
    public List<BuildingResponse> getAllBuildings() {
        return buildingRepository.findAllByDeletedFalse().stream()
                .map(buildingMapper::toBuildingResponse)
                .toList();
    }

    @Override
    public Page<BuildingResponse> getAllBuildings(Pageable pageable) {
        return buildingRepository.findAllByDeletedFalse(pageable)
                .map(buildingMapper::toBuildingResponse);
    }

    @Override
    public BuildingResponse updateBuilding(Long id, BuildingRequest request) {
        Building building = buildingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        buildingMapper.updateBuilding(building, request);
        building = buildingRepository.save(building);
        return buildingMapper.toBuildingResponse(building);
    }

    @Override
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        buildingRepository.deleteById(id);
    }
}
