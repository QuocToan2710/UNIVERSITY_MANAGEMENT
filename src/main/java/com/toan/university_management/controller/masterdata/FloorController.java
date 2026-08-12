package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.FloorRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.FloorResponse;
import com.toan.university_management.service.masterdata.floor.FloorService;
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
@RequestMapping("/floors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FloorController {
    FloorService floorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<FloorResponse> createFloor(@Valid @RequestBody FloorRequest request) {
        return ApiResponse.<FloorResponse>builder()
                .message("Successfully created floor")
                .result(floorService.createFloor(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<FloorResponse>> getAllFloors(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<FloorResponse>>builder()
                .result(floorService.getAllFloors(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<FloorResponse>> getAllList() {
        return ApiResponse.<List<FloorResponse>>builder()
                .result(floorService.getAllFloors())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<FloorResponse> getFloorById(@PathVariable Long id) {
        return ApiResponse.<FloorResponse>builder()
                .result(floorService.getFloorById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<FloorResponse> updateFloor(@PathVariable Long id, @Valid @RequestBody FloorRequest request) {
        return ApiResponse.<FloorResponse>builder()
                .result(floorService.updateFloor(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteFloor(@PathVariable Long id) {
        floorService.deleteFloor(id);
        return ApiResponse.<String>builder().result("Floor has been deleted successfully").build();
    }
}
