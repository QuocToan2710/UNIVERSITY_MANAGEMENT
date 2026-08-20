package com.toan.university_management.service.masterdata.province;

import com.toan.university_management.dto.request.masterdata.ProvinceRequest;
import com.toan.university_management.dto.response.masterdata.ProvinceResponse;
import com.toan.university_management.dto.request.masterdata.ProvinceSearchPaginationRQ;
import com.toan.university_management.dto.response.BasePaginationRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProvinceService {
    ProvinceResponse createProvince(ProvinceRequest request);
    ProvinceResponse getProvinceById(Long id);
    List<ProvinceResponse> getAllProvinces();
    Page<ProvinceResponse> getAllProvinces(Pageable pageable);
    ProvinceResponse updateProvince(Long id, ProvinceRequest request);
    void deleteProvince(Long id);
    BasePaginationRS<ProvinceResponse> search(ProvinceSearchPaginationRQ search);
    List<ProvinceResponse> export(ProvinceSearchPaginationRQ search);
}
