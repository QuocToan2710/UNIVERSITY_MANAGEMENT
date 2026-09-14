package com.toan.university_management.service.masterdata.province;

import com.toan.university_management.model.masterdata.ProvinceRequest;
import com.toan.university_management.model.masterdata.ProvinceResponse;
import com.toan.university_management.model.masterdata.ProvinceSearchPaginationRQ;
import com.toan.university_management.common.dto.BasePaginationRS;
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
