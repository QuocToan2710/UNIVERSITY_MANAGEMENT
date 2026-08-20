package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.DistrictRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.DistrictResponse;
import com.toan.university_management.service.masterdata.district.DistrictService;
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
@RequestMapping("/districts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DistrictController {

    DistrictService districtService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<DistrictResponse> createDistrict(@Valid @RequestBody DistrictRequest request) {
        return ApiResponse.<DistrictResponse>builder()
                .message("Successfully created district")
                .result(districtService.createDistrict(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<DistrictResponse>> getAllDistricts(@RequestParam(required = false) Long provinceId, @PageableDefault(page = 0, size = 10, sort = "districtName") Pageable pageable) {
        return ApiResponse.<Page<DistrictResponse>>builder()
                .result(districtService.getAllDistricts(provinceId, pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<DistrictResponse>> getAllList(@RequestParam(required = false) Long provinceId) {
        return ApiResponse.<List<DistrictResponse>>builder()
                .result(districtService.getAllDistricts(provinceId))
                .build();
    }

    @GetMapping("/by-province/{provinceId}")
    ApiResponse<List<DistrictResponse>> getByProvince(@PathVariable Long provinceId) {
        return ApiResponse.<List<DistrictResponse>>builder()
                .result(districtService.getAllDistricts(provinceId))
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<DistrictResponse> getDistrictById(@PathVariable Long id) {
        return ApiResponse.<DistrictResponse>builder()
                .result(districtService.getDistrictById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<DistrictResponse> updateDistrict(@PathVariable Long id, @Valid @RequestBody DistrictRequest request) {
        return ApiResponse.<DistrictResponse>builder()
                .message("Successfully updated district")
                .result(districtService.updateDistrict(id, request))
                .build();
    }

    @PostMapping("/search")
    ApiResponse<com.toan.university_management.dto.response.BasePaginationRS<DistrictResponse>> searchDistricts(
            @RequestBody(required = false) com.toan.university_management.dto.request.masterdata.DistrictSearchPaginationRQ request) {
        return ApiResponse.<com.toan.university_management.dto.response.BasePaginationRS<DistrictResponse>>builder()
                .result(districtService.search(request))
                .build();
    }

    @PostMapping("/export")
    ApiResponse<List<DistrictResponse>> exportDistricts(
            @RequestBody(required = false) com.toan.university_management.dto.request.masterdata.DistrictSearchPaginationRQ request) {
        return ApiResponse.<List<DistrictResponse>>builder()
                .result(districtService.export(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteDistrict(@PathVariable Long id) {
        districtService.deleteDistrict(id);
        return ApiResponse.<String>builder().result("District has been deleted successfully").build();
    }
}
