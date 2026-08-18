package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.BuildingRequest;
import com.toan.university_management.dto.request.masterdata.BuildingSearchPaginationRQ;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.BasePaginationRS;
import com.toan.university_management.dto.response.masterdata.BuildingResponse;
import com.toan.university_management.service.masterdata.building.BuildingService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BuildingController {
    BuildingService buildingService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<BuildingResponse> createBuilding(@Valid @RequestBody BuildingRequest request) {
        return ApiResponse.<BuildingResponse>builder()
                .message("Successfully created building")
                .result(buildingService.createBuilding(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<BuildingResponse>> getAllBuildings(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<BuildingResponse>>builder()
                .result(buildingService.getAllBuildings(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<BuildingResponse>> getAllList() {
        return ApiResponse.<List<BuildingResponse>>builder()
                .result(buildingService.getAllBuildings())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<BuildingResponse> getBuildingById(@PathVariable Long id) {
        return ApiResponse.<BuildingResponse>builder()
                .result(buildingService.getBuildingById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<BuildingResponse> updateBuilding(@PathVariable Long id, @Valid @RequestBody BuildingRequest request) {
        return ApiResponse.<BuildingResponse>builder()
                .result(buildingService.updateBuilding(id, request))
                .build();
    }

    @PostMapping("/search")
    ApiResponse<BasePaginationRS<BuildingResponse>> searchBuildings(@RequestBody(required = false) BuildingSearchPaginationRQ request) {
        return ApiResponse.<BasePaginationRS<BuildingResponse>>builder()
                .result(buildingService.search(request))
                .build();
    }

    @PostMapping("/export")
    ApiResponse<List<BuildingResponse>> exportBuildings(@RequestBody(required = false) BuildingSearchPaginationRQ request) {
        return ApiResponse.<List<BuildingResponse>>builder()
                .result(buildingService.export(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteBuilding(@PathVariable Long id) {
        buildingService.deleteBuilding(id);
        return ApiResponse.<String>builder().result("Building has been deleted successfully").build();
    }
}
