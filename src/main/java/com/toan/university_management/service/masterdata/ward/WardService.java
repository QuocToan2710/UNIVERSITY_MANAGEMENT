package com.toan.university_management.service.masterdata.ward;

import com.toan.university_management.dto.request.masterdata.WardRequest;
import com.toan.university_management.dto.response.masterdata.WardResponse;
import com.toan.university_management.dto.request.masterdata.WardSearchPaginationRQ;
import com.toan.university_management.dto.response.BasePaginationRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WardService {
    WardResponse createWard(WardRequest request);
    WardResponse getWardById(Long id);
    List<WardResponse> getAllWards(Long districtId);
    Page<WardResponse> getAllWards(Long districtId, Pageable pageable);
    WardResponse updateWard(Long id, WardRequest request);
    void deleteWard(Long id);
    BasePaginationRS<WardResponse> search(WardSearchPaginationRQ search);
    List<WardResponse> export(WardSearchPaginationRQ search);
}
