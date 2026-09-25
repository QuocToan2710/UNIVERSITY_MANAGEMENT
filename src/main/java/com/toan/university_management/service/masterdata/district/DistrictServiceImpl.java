package com.toan.university_management.service.masterdata.district;

import com.toan.university_management.model.masterdata.DistrictRequest;
import com.toan.university_management.model.masterdata.DistrictSearchPaginationRQ;
import com.toan.university_management.common.dto.BasePaginationRS;
import com.toan.university_management.model.masterdata.DistrictResponse;
import com.toan.university_management.entity.masterdata.District;
import com.toan.university_management.entity.masterdata.Province;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.repository.masterdata.DistrictRepository;
import com.toan.university_management.repository.masterdata.ProvinceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class DistrictServiceImpl implements DistrictService {

    DistrictRepository districtRepository;
    ProvinceRepository provinceRepository;

    @Override
    public DistrictResponse createDistrict(DistrictRequest request) {
        if (!provinceRepository.existsByIdAndDeletedFalse(request.getProvinceId())) {
            throw new AppException(ErrorCode.PROVINCE_NOT_FOUND);
        }
        if (districtRepository.existsByDistrictCodeAndDeletedFalse(request.getDistrictCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        District district = District.builder()
                .districtCode(request.getDistrictCode().trim())
                .districtName(request.getDistrictName().trim())
                .districtType(request.getDistrictType())
                .provinceId(request.getProvinceId())
                .deleted(false)
                .build();
        district = districtRepository.save(district);
        return enrichResponse(district);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictResponse getDistrictById(Long id) {
        District district = districtRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISTRICT_NOT_FOUND));
        return enrichResponse(district);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictResponse> getAllDistricts(Long provinceId) {
        List<District> list;
        if (provinceId != null) {
            list = districtRepository.findAllByProvinceIdAndDeletedFalseOrderByDistrictNameAsc(provinceId);
        } else {
            list = districtRepository.findAllByDeletedFalseOrderByDistrictNameAsc();
        }
        return enrichResponses(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictResponse> getAllDistricts(Long provinceId, Pageable pageable) {
        Page<District> districtPage;
        if (provinceId != null) {
            districtPage = districtRepository.findAllByProvinceIdAndDeletedFalseOrderByDistrictNameAsc(provinceId, pageable);
        } else {
            districtPage = districtRepository.findAllByDeletedFalseOrderByDistrictNameAsc(pageable);
        }
        List<DistrictResponse> content = enrichResponses(districtPage.getContent());
        return new PageImpl<>(content, pageable, districtPage.getTotalElements());
    }

    @Override
    public DistrictResponse updateDistrict(Long id, DistrictRequest request) {
        District district = districtRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.DISTRICT_NOT_FOUND));

        if (!provinceRepository.existsByIdAndDeletedFalse(request.getProvinceId())) {
            throw new AppException(ErrorCode.PROVINCE_NOT_FOUND);
        }

        if (!district.getDistrictCode().equalsIgnoreCase(request.getDistrictCode().trim())
                && districtRepository.existsByDistrictCodeAndDeletedFalse(request.getDistrictCode().trim())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        district.setDistrictCode(request.getDistrictCode().trim());
        district.setDistrictName(request.getDistrictName().trim());
        district.setDistrictType(request.getDistrictType());
        district.setProvinceId(request.getProvinceId());
        district = districtRepository.save(district);
        return enrichResponse(district);
    }

    @Override
    public void deleteDistrict(Long id) {
        if (!districtRepository.existsById(id)) {
            throw new AppException(ErrorCode.DISTRICT_NOT_FOUND);
        }
        districtRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationRS<DistrictResponse> search(DistrictSearchPaginationRQ search) {
        if (search == null) search = new DistrictSearchPaginationRQ();
        int page = Math.max(0, search.getPageNumber());
        int size = search.getPageSize() > 0 ? search.getPageSize() : 10;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "districtName"));

        org.springframework.data.jpa.domain.Specification<District> spec = com.toan.university_management.specification.masterdata.DistrictSpecification.filter(search);
        org.springframework.data.domain.Page<District> districtPage = districtRepository.findAll(spec, pageable);
        List<DistrictResponse> content = enrichResponses(districtPage.getContent());

        return BasePaginationRS.<DistrictResponse>builder()
                .items(content)
                .totalCount(districtPage.getTotalElements())
                .totalPage(districtPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictResponse> export(DistrictSearchPaginationRQ search) {
        org.springframework.data.jpa.domain.Specification<District> spec = com.toan.university_management.specification.masterdata.DistrictSpecification.filter(search);
        List<District> districts = districtRepository.findAll(spec, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "districtName"));
        return enrichResponses(districts);
    }

    private DistrictResponse enrichResponse(District d) {
        DistrictResponse res = DistrictResponse.builder()
                .id(d.getId())
                .districtCode(d.getDistrictCode())
                .districtName(d.getDistrictName())
                .districtType(d.getDistrictType())
                .provinceId(d.getProvinceId())
                .build();
        if (d.getProvinceId() != null) {
            provinceRepository.findByIdAndDeletedFalse(d.getProvinceId()).ifPresent(p -> res.setProvinceName(p.getProvinceName()));
        }
        return res;
    }

    private List<DistrictResponse> enrichResponses(List<District> districts) {
        if (districts.isEmpty()) return Collections.emptyList();
        Set<Long> provIds = districts.stream().map(District::getProvinceId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Province> provMap = provinceRepository.findAllByIdInAndDeletedFalse(provIds)
                .stream().collect(Collectors.toMap(Province::getId, Function.identity()));

        return districts.stream().map(d -> {
            DistrictResponse res = DistrictResponse.builder()
                    .id(d.getId())
                    .districtCode(d.getDistrictCode())
                    .districtName(d.getDistrictName())
                    .districtType(d.getDistrictType())
                    .provinceId(d.getProvinceId())
                    .build();
            if (d.getProvinceId() != null && provMap.containsKey(d.getProvinceId())) {
                res.setProvinceName(provMap.get(d.getProvinceId()).getProvinceName());
            }
            return res;
        }).toList();
    }
}
