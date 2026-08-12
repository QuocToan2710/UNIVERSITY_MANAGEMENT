package com.toan.university_management.service.masterdata.floor;

import com.toan.university_management.dto.request.masterdata.FloorRequest;
import com.toan.university_management.dto.response.masterdata.FloorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FloorService {
    FloorResponse createFloor(FloorRequest request);
    FloorResponse getFloorById(Long id);
    List<FloorResponse> getAllFloors();
    Page<FloorResponse> getAllFloors(Pageable pageable);
    FloorResponse updateFloor(Long id, FloorRequest request);
    void deleteFloor(Long id);
}
