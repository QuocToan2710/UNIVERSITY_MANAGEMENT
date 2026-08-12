package com.toan.university_management.service.masterdata.floor;

import com.toan.university_management.dto.request.masterdata.FloorRequest;
import com.toan.university_management.dto.response.masterdata.FloorResponse;
import com.toan.university_management.entity.masterdata.Building;
import com.toan.university_management.entity.masterdata.Floor;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.FloorMapper;
import com.toan.university_management.repository.masterdata.BuildingRepository;
import com.toan.university_management.repository.masterdata.FloorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class FloorServiceImpl implements FloorService {
    FloorRepository floorRepository;
    BuildingRepository buildingRepository;
    FloorMapper floorMapper;

    @Override
    public FloorResponse createFloor(FloorRequest request) {
        if (floorRepository.existsByFloorCodeAndDeletedFalse(request.getFloorCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getBuildingId() != null && !buildingRepository.existsByIdAndDeletedFalse(request.getBuildingId())) {
            throw new AppException(ErrorCode.BUILDING_NOT_FOUND);
        }
        Floor floor = floorMapper.toFloor(request);
        if (floor.getStatus() == null || floor.getStatus().isBlank()) {
            floor.setStatus("ACTIVE");
        }
        floor = floorRepository.save(floor);
        return enrichResponse(floor);
    }

    @Override
    public FloorResponse getFloorById(Long id) {
        Floor floor = floorRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return enrichResponse(floor);
    }

    @Override
    public List<FloorResponse> getAllFloors() {
        return enrichResponses(floorRepository.findAllByDeletedFalse());
    }

    @Override
    public Page<FloorResponse> getAllFloors(Pageable pageable) {
        Page<Floor> page = floorRepository.findAllByDeletedFalse(pageable);
        List<FloorResponse> content = enrichResponses(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public FloorResponse updateFloor(Long id, FloorRequest request) {
        Floor floor = floorRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        if (request.getBuildingId() != null && !buildingRepository.existsByIdAndDeletedFalse(request.getBuildingId())) {
            throw new AppException(ErrorCode.BUILDING_NOT_FOUND);
        }
        floorMapper.updateFloor(floor, request);
        floor = floorRepository.save(floor);
        return enrichResponse(floor);
    }

    @Override
    public void deleteFloor(Long id) {
        if (!floorRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        floorRepository.deleteById(id);
    }

    private FloorResponse enrichResponse(Floor item) {
        FloorResponse res = floorMapper.toFloorResponse(item);
        if (item.getBuildingId() != null) {
            buildingRepository.findByIdAndDeletedFalse(item.getBuildingId()).ifPresent(b -> {
                res.setBuildingName(b.getName());
            });
        }
        return res;
    }

    private List<FloorResponse> enrichResponses(List<Floor> items) {
        if (items.isEmpty()) return Collections.emptyList();

        Set<Long> buildingIds = items.stream().map(Floor::getBuildingId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Building> buildingMap = buildingRepository.findAllById(buildingIds)
                .stream().collect(Collectors.toMap(Building::getId, Function.identity()));

        return items.stream().map(item -> {
            FloorResponse res = floorMapper.toFloorResponse(item);
            if (item.getBuildingId() != null && buildingMap.containsKey(item.getBuildingId())) {
                res.setBuildingName(buildingMap.get(item.getBuildingId()).getName());
            }
            return res;
        }).toList();
    }
}
