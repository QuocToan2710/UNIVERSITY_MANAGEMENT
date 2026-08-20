package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.WardRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.WardResponse;
import com.toan.university_management.service.masterdata.ward.WardService;
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
@RequestMapping("/wards")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WardController {

    WardService wardService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<WardResponse> createWard(@Valid @RequestBody WardRequest request) {
        return ApiResponse.<WardResponse>builder()
                .message("Successfully created ward")
                .result(wardService.createWard(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<WardResponse>> getAllWards(
            @RequestParam(required = false) Long districtId,
            @PageableDefault(page = 0, size = 10, sort = "wardName") Pageable pageable) {
        return ApiResponse.<Page<WardResponse>>builder()
                .result(wardService.getAllWards(districtId, pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<WardResponse>> getAllList(@RequestParam(required = false) Long districtId) {
        return ApiResponse.<List<WardResponse>>builder()
                .result(wardService.getAllWards(districtId))
                .build();
    }

    @GetMapping("/by-district/{districtId}")
    ApiResponse<List<WardResponse>> getByDistrict(@PathVariable Long districtId) {
        return ApiResponse.<List<WardResponse>>builder()
                .result(wardService.getAllWards(districtId))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<WardResponse> getWardById(@PathVariable Long id) {
        return ApiResponse.<WardResponse>builder()
                .result(wardService.getWardById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<WardResponse> updateWard(@PathVariable Long id, @Valid @RequestBody WardRequest request) {
        return ApiResponse.<WardResponse>builder()
                .message("Successfully updated ward")
                .result(wardService.updateWard(id, request))
                .build();
    }

    @PostMapping("/search")
    ApiResponse<com.toan.university_management.dto.response.BasePaginationRS<WardResponse>> searchWards(
            @RequestBody(required = false) com.toan.university_management.dto.request.masterdata.WardSearchPaginationRQ request) {
        return ApiResponse.<com.toan.university_management.dto.response.BasePaginationRS<WardResponse>>builder()
                .result(wardService.search(request))
                .build();
    }

    @PostMapping("/export")
    ApiResponse<List<WardResponse>> exportWards(
            @RequestBody(required = false) com.toan.university_management.dto.request.masterdata.WardSearchPaginationRQ request) {
        return ApiResponse.<List<WardResponse>>builder()
                .result(wardService.export(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteWard(@PathVariable Long id) {
        wardService.deleteWard(id);
        return ApiResponse.<String>builder().result("Ward has been deleted successfully").build();
    }
}
