package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.ProvinceRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.ProvinceResponse;
import com.toan.university_management.service.masterdata.province.ProvinceService;
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
@RequestMapping("/provinces")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProvinceController {

    ProvinceService provinceService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProvinceResponse> createProvince(@Valid @RequestBody ProvinceRequest request) {
        return ApiResponse.<ProvinceResponse>builder()
                .message("Successfully created province")
                .result(provinceService.createProvince(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<ProvinceResponse>> getAllProvinces(
            @PageableDefault(page = 0, size = 10, sort = "provinceName") Pageable pageable) {
        return ApiResponse.<Page<ProvinceResponse>>builder()
                .result(provinceService.getAllProvinces(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<ProvinceResponse>> getAllList() {
        return ApiResponse.<List<ProvinceResponse>>builder()
                .result(provinceService.getAllProvinces())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ProvinceResponse> getProvinceById(@PathVariable Long id) {
        return ApiResponse.<ProvinceResponse>builder()
                .result(provinceService.getProvinceById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProvinceResponse> updateProvince(@PathVariable Long id, @Valid @RequestBody ProvinceRequest request) {
        return ApiResponse.<ProvinceResponse>builder()
                .message("Successfully updated province")
                .result(provinceService.updateProvince(id, request))
                .build();
    }

    @PostMapping("/search")
    ApiResponse<com.toan.university_management.dto.response.BasePaginationRS<ProvinceResponse>> searchProvinces(
            @RequestBody(required = false) com.toan.university_management.dto.request.masterdata.ProvinceSearchPaginationRQ request) {
        return ApiResponse.<com.toan.university_management.dto.response.BasePaginationRS<ProvinceResponse>>builder()
                .result(provinceService.search(request))
                .build();
    }

    @PostMapping("/export")
    ApiResponse<List<ProvinceResponse>> exportProvinces(
            @RequestBody(required = false) com.toan.university_management.dto.request.masterdata.ProvinceSearchPaginationRQ request) {
        return ApiResponse.<List<ProvinceResponse>>builder()
                .result(provinceService.export(request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteProvince(@PathVariable Long id) {
        provinceService.deleteProvince(id);
        return ApiResponse.<String>builder().result("Province has been deleted successfully").build();
    }
}
