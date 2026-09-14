package com.toan.university_management.service.masterdata.building;

import com.toan.university_management.model.masterdata.BuildingRequest;
import com.toan.university_management.model.masterdata.BuildingSearchPaginationRQ;
import com.toan.university_management.common.dto.BasePaginationRS;
import com.toan.university_management.model.masterdata.BuildingResponse;
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
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        }
        Building building = buildingMapper.toBuilding(request);
        if (building.getStatus() == null || building.getStatus().isBlank()) {
            building.setStatus("ACTIVE");
        }
        building = buildingRepository.save(building);
        return buildingMapper.toBuildingResponse(building);
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingResponse getBuildingById(Long id) {
        Building building = buildingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        return buildingMapper.toBuildingResponse(building);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingResponse> getAllBuildings() {
        return buildingRepository.findAllByDeletedFalse().stream()
                .map(buildingMapper::toBuildingResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuildingResponse> getAllBuildings(Pageable pageable) {
        return buildingRepository.findAllByDeletedFalse(pageable)
                .map(buildingMapper::toBuildingResponse);
    }

    @Override
    public BuildingResponse updateBuilding(Long id, BuildingRequest request) {
        Building building = buildingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.BUILDING_NOT_FOUND));
        buildingMapper.updateBuilding(building, request);
        building = buildingRepository.save(building);
        return buildingMapper.toBuildingResponse(building);
    }

    @Override
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.BUILDING_NOT_FOUND);
        }
        buildingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationRS<BuildingResponse> search(BuildingSearchPaginationRQ search) {
        if (search == null) search = new BuildingSearchPaginationRQ();
        int page = Math.max(0, search.getPageNumber());
        int size = search.getPageSize() > 0 ? search.getPageSize() : 10;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));

        org.springframework.data.jpa.domain.Specification<Building> spec = com.toan.university_management.specification.masterdata.BuildingSpecification.filter(search);
        org.springframework.data.domain.Page<Building> buildingPage = buildingRepository.findAll(spec, pageable);
        List<BuildingResponse> content = buildingPage.getContent().stream()
                .map(buildingMapper::toBuildingResponse)
                .toList();

        return BasePaginationRS.<BuildingResponse>builder()
                .items(content)
                .totalCount(buildingPage.getTotalElements())
                .totalPage(buildingPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BuildingResponse> export(BuildingSearchPaginationRQ search) {
        org.springframework.data.jpa.domain.Specification<Building> spec = com.toan.university_management.specification.masterdata.BuildingSpecification.filter(search);
        return buildingRepository.findAll(spec, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "buildingCode"))
                .stream()
                .map(buildingMapper::toBuildingResponse)
                .toList();
    }
}
