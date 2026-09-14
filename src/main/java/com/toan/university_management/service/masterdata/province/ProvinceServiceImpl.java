package com.toan.university_management.service.masterdata.province;

import com.toan.university_management.model.masterdata.ProvinceRequest;
import com.toan.university_management.model.masterdata.ProvinceSearchPaginationRQ;
import com.toan.university_management.common.dto.BasePaginationRS;
import com.toan.university_management.model.masterdata.ProvinceResponse;
import com.toan.university_management.entity.masterdata.Province;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.repository.masterdata.ProvinceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class ProvinceServiceImpl implements ProvinceService {

    ProvinceRepository provinceRepository;

    @Override
    public ProvinceResponse createProvince(ProvinceRequest request) {
        if (provinceRepository.existsByProvinceCodeAndDeletedFalse(request.getProvinceCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Province province = Province.builder()
                .provinceCode(request.getProvinceCode().trim())
                .provinceName(request.getProvinceName().trim())
                .provinceType(request.getProvinceType())
                .deleted(false)
                .build();
        province = provinceRepository.save(province);
        return toResponse(province);
    }

    @Override
    @Transactional(readOnly = true)
    public ProvinceResponse getProvinceById(Long id) {
        Province province = provinceRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND));
        return toResponse(province);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvinceResponse> getAllProvinces() {
        return provinceRepository.findAllByDeletedFalseOrderByProvinceNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProvinceResponse> getAllProvinces(Pageable pageable) {
        return provinceRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public ProvinceResponse updateProvince(Long id, ProvinceRequest request) {
        Province province = provinceRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROVINCE_NOT_FOUND));

        if (!province.getProvinceCode().equalsIgnoreCase(request.getProvinceCode().trim())
                && provinceRepository.existsByProvinceCodeAndDeletedFalse(request.getProvinceCode().trim())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        province.setProvinceCode(request.getProvinceCode().trim());
        province.setProvinceName(request.getProvinceName().trim());
        province.setProvinceType(request.getProvinceType());
        province = provinceRepository.save(province);
        return toResponse(province);
    }

    @Override
    public void deleteProvince(Long id) {
        if (!provinceRepository.existsById(id)) {
            throw new AppException(ErrorCode.PROVINCE_NOT_FOUND);
        }
        provinceRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationRS<ProvinceResponse> search(ProvinceSearchPaginationRQ search) {
        if (search == null) search = new ProvinceSearchPaginationRQ();
        int page = Math.max(0, search.getPageNumber());
        int size = search.getPageSize() > 0 ? search.getPageSize() : 10;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "provinceName"));

        org.springframework.data.jpa.domain.Specification<Province> spec = com.toan.university_management.specification.masterdata.ProvinceSpecification.filter(search);
        org.springframework.data.domain.Page<Province> provincePage = provinceRepository.findAll(spec, pageable);
        List<ProvinceResponse> content = provincePage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return BasePaginationRS.<ProvinceResponse>builder()
                .items(content)
                .totalCount(provincePage.getTotalElements())
                .totalPage(provincePage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvinceResponse> export(ProvinceSearchPaginationRQ search) {
        org.springframework.data.jpa.domain.Specification<Province> spec = com.toan.university_management.specification.masterdata.ProvinceSpecification.filter(search);
        return provinceRepository.findAll(spec, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "provinceName"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ProvinceResponse toResponse(Province p) {
        return ProvinceResponse.builder()
                .id(p.getId())
                .provinceCode(p.getProvinceCode())
                .provinceName(p.getProvinceName())
                .provinceType(p.getProvinceType())
                .build();
    }
}
