package com.toan.university_management.service.masterdata.district;

import com.toan.university_management.dto.request.masterdata.DistrictRequest;
import com.toan.university_management.dto.response.masterdata.DistrictResponse;
import com.toan.university_management.dto.request.masterdata.DistrictSearchPaginationRQ;
import com.toan.university_management.dto.response.BasePaginationRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DistrictService {
    DistrictResponse createDistrict(DistrictRequest request);
    DistrictResponse getDistrictById(Long id);
    List<DistrictResponse> getAllDistricts(Long provinceId);
    Page<DistrictResponse> getAllDistricts(Long provinceId, Pageable pageable);
    DistrictResponse updateDistrict(Long id, DistrictRequest request);
    void deleteDistrict(Long id);
    BasePaginationRS<DistrictResponse> search(DistrictSearchPaginationRQ search);
    List<DistrictResponse> export(DistrictSearchPaginationRQ search);
}
