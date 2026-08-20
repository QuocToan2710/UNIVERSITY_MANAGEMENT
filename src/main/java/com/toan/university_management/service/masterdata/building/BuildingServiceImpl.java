package com.toan.university_management.service.masterdata.building;

import com.toan.university_management.dto.request.masterdata.BuildingRequest;
import com.toan.university_management.dto.request.masterdata.BuildingSearchPaginationRQ;
import com.toan.university_management.dto.response.BasePaginationRS;
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

        String kw = search.getKeyword() != null ? search.getKeyword().trim().toLowerCase() : "";
        String codeFilter = search.getBuildingCode() != null ? search.getBuildingCode().trim().toLowerCase() : "";
        String nameFilter = search.getName() != null ? search.getName().trim().toLowerCase() : "";
        String statusFilter = search.getStatus() != null ? search.getStatus().trim().toLowerCase() : "";

        List<BuildingResponse> all = buildingRepository.findAllByDeletedFalse().stream()
                .map(buildingMapper::toBuildingResponse)
                .filter(b -> {
                    if (!kw.isEmpty()) {
                        String full = ((b.getBuildingCode() != null ? b.getBuildingCode() : "") + " "
                                + (b.getName() != null ? b.getName() : "") + " "
                                + (b.getDescription() != null ? b.getDescription() : "")).toLowerCase();
                        if (!full.contains(kw)) return false;
                    }
                    if (!codeFilter.isEmpty()) {
                        if (b.getBuildingCode() == null || !b.getBuildingCode().toLowerCase().contains(codeFilter)) return false;
                    }
                    if (!nameFilter.isEmpty()) {
                        if (b.getName() == null || !b.getName().toLowerCase().contains(nameFilter)) return false;
                    }
                    if (!statusFilter.isEmpty() && !"all".equalsIgnoreCase(statusFilter)) {
                        if (b.getStatus() == null || !b.getStatus().equalsIgnoreCase(statusFilter)) return false;
                    }
                    return true;
                })
                .toList();

        long count = all.size();
        int start = page * size;
        List<BuildingResponse> pageList = start < count ? all.subList(start, Math.min(start + size, (int) count)) : List.of();

        int totalPage = (int) (count / size);
        if (count % size != 0) totalPage++;

        BasePaginationRS<BuildingResponse> outputs = new BasePaginationRS<>();
        outputs.setItems(pageList);
        outputs.setTotalCount(count);
        outputs.setTotalPage(totalPage);
        return outputs;
    }

    @Override
    public List<BuildingResponse> export(BuildingSearchPaginationRQ search) {
        BuildingSearchPaginationRQ copy = search != null ? search : new BuildingSearchPaginationRQ();
        copy.setPageNumber(0);
        copy.setPageSize(Integer.MAX_VALUE);
        return search(copy).getItems();
    }
}
