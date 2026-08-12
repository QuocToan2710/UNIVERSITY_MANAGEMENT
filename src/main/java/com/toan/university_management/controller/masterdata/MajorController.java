package com.toan.university_management.controller.masterdata;

import com.toan.university_management.dto.request.masterdata.MajorRequest;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.dto.response.masterdata.MajorResponse;
import com.toan.university_management.service.masterdata.major.MajorService;
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
@RequestMapping("/majors")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MajorController {
    MajorService majorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<MajorResponse> createMajor(@Valid @RequestBody MajorRequest request) {
        return ApiResponse.<MajorResponse>builder()
                .message("Successfully created major")
                .result(majorService.createMajor(request))
                .build();
    }

    @GetMapping
    ApiResponse<Page<MajorResponse>> getAllMajors(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ApiResponse.<Page<MajorResponse>>builder()
                .result(majorService.getAllMajors(pageable))
                .build();
    }

    @GetMapping("/all")
    ApiResponse<List<MajorResponse>> getAllList() {
        return ApiResponse.<List<MajorResponse>>builder()
                .result(majorService.getAllMajors())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<MajorResponse> getMajorById(@PathVariable Long id) {
        return ApiResponse.<MajorResponse>builder()
                .result(majorService.getMajorById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<MajorResponse> updateMajor(@PathVariable Long id, @Valid @RequestBody MajorRequest request) {
        return ApiResponse.<MajorResponse>builder()
                .result(majorService.updateMajor(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<String> deleteMajor(@PathVariable Long id) {
        majorService.deleteMajor(id);
        return ApiResponse.<String>builder().result("Major has been deleted successfully").build();
    }
}
