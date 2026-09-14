package com.toan.university_management.service.masterdata.ward;

import com.toan.university_management.model.masterdata.WardRequest;
import com.toan.university_management.model.masterdata.WardSearchPaginationRQ;
import com.toan.university_management.common.dto.BasePaginationRS;
import com.toan.university_management.model.masterdata.WardResponse;
import com.toan.university_management.entity.masterdata.District;
import com.toan.university_management.entity.masterdata.Province;
import com.toan.university_management.entity.masterdata.Ward;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.repository.masterdata.DistrictRepository;
import com.toan.university_management.repository.masterdata.ProvinceRepository;
import com.toan.university_management.repository.masterdata.WardRepository;
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
public class WardServiceImpl implements WardService {

    WardRepository wardRepository;
    DistrictRepository districtRepository;
    ProvinceRepository provinceRepository;

    @Override
    public WardResponse createWard(WardRequest request) {
        if (!districtRepository.existsByIdAndDeletedFalse(request.getDistrictId())) {
            throw new AppException(ErrorCode.DISTRICT_NOT_FOUND);
        }
        if (wardRepository.existsByWardCodeAndDeletedFalse(request.getWardCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        Ward ward = Ward.builder()
                .wardCode(request.getWardCode().trim())
                .wardName(request.getWardName().trim())
                .wardType(request.getWardType())
                .districtId(request.getDistrictId())
                .deleted(false)
                .build();
        ward = wardRepository.save(ward);
        return enrichResponse(ward);
    }

    @Override
    @Transactional(readOnly = true)
    public WardResponse getWardById(Long id) {
        Ward ward = wardRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND));
        return enrichResponse(ward);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WardResponse> getAllWards(Long districtId) {
        List<Ward> list;
        if (districtId != null) {
            list = wardRepository.findAllByDistrictIdAndDeletedFalseOrderByWardNameAsc(districtId);
        } else {
            list = wardRepository.findAllByDeletedFalseOrderByWardNameAsc();
        }
        return enrichResponses(list);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WardResponse> getAllWards(Long districtId, Pageable pageable) {
        List<WardResponse> all = getAllWards(districtId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), all.size());
        List<WardResponse> paged = (start <= end) ? all.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(paged, pageable, all.size());
    }

    @Override
    public WardResponse updateWard(Long id, WardRequest request) {
        Ward ward = wardRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.WARD_NOT_FOUND));

        if (!districtRepository.existsByIdAndDeletedFalse(request.getDistrictId())) {
            throw new AppException(ErrorCode.DISTRICT_NOT_FOUND);
        }

        if (!ward.getWardCode().equalsIgnoreCase(request.getWardCode().trim())
                && wardRepository.existsByWardCodeAndDeletedFalse(request.getWardCode().trim())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        ward.setWardCode(request.getWardCode().trim());
        ward.setWardName(request.getWardName().trim());
        ward.setWardType(request.getWardType());
        ward.setDistrictId(request.getDistrictId());
        ward = wardRepository.save(ward);
        return enrichResponse(ward);
    }

    @Override
    public void deleteWard(Long id) {
        if (!wardRepository.existsById(id)) {
            throw new AppException(ErrorCode.WARD_NOT_FOUND);
        }
        wardRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BasePaginationRS<WardResponse> search(WardSearchPaginationRQ search) {
        if (search == null) search = new WardSearchPaginationRQ();
        int page = Math.max(0, search.getPageNumber());
        int size = search.getPageSize() > 0 ? search.getPageSize() : 10;
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "wardName"));

        Set<Long> matchedDistrictIds = null;
        if (search.getProvinceId() != null && search.getProvinceId() > 0 && (search.getDistrictId() == null || search.getDistrictId() <= 0)) {
            matchedDistrictIds = districtRepository.findAllByProvinceIdAndDeletedFalseOrderByDistrictNameAsc(search.getProvinceId())
                    .stream().map(District::getId).collect(Collectors.toSet());
        }

        org.springframework.data.jpa.domain.Specification<Ward> spec = com.toan.university_management.specification.masterdata.WardSpecification.filter(search, matchedDistrictIds);
        org.springframework.data.domain.Page<Ward> wardPage = wardRepository.findAll(spec, pageable);
        List<WardResponse> content = enrichResponses(wardPage.getContent());

        return BasePaginationRS.<WardResponse>builder()
                .items(content)
                .totalCount(wardPage.getTotalElements())
                .totalPage(wardPage.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WardResponse> export(WardSearchPaginationRQ search) {
        Set<Long> matchedDistrictIds = null;
        if (search != null && search.getProvinceId() != null && search.getProvinceId() > 0 && (search.getDistrictId() == null || search.getDistrictId() <= 0)) {
            matchedDistrictIds = districtRepository.findAllByProvinceIdAndDeletedFalseOrderByDistrictNameAsc(search.getProvinceId())
                    .stream().map(District::getId).collect(Collectors.toSet());
        }
        org.springframework.data.jpa.domain.Specification<Ward> spec = com.toan.university_management.specification.masterdata.WardSpecification.filter(search, matchedDistrictIds);
        List<Ward> wards = wardRepository.findAll(spec, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "wardName"));
        return enrichResponses(wards);
    }

    private WardResponse enrichResponse(Ward w) {
        WardResponse res = WardResponse.builder()
                .id(w.getId())
                .wardCode(w.getWardCode())
                .wardName(w.getWardName())
                .wardType(w.getWardType())
                .districtId(w.getDistrictId())
                .build();
        if (w.getDistrictId() != null) {
            districtRepository.findByIdAndDeletedFalse(w.getDistrictId()).ifPresent(d -> {
                res.setDistrictName(d.getDistrictName());
                res.setProvinceId(d.getProvinceId());
                if (d.getProvinceId() != null) {
                    provinceRepository.findByIdAndDeletedFalse(d.getProvinceId())
                            .ifPresent(p -> res.setProvinceName(p.getProvinceName()));
                }
            });
        }
        return res;
    }

    private List<WardResponse> enrichResponses(List<Ward> wards) {
        if (wards.isEmpty()) return Collections.emptyList();

        Set<Long> distIds = wards.stream().map(Ward::getDistrictId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, District> distMap = districtRepository.findAllByIdInAndDeletedFalse(distIds)
                .stream().collect(Collectors.toMap(District::getId, Function.identity()));

        Set<Long> provIds = distMap.values().stream().map(District::getProvinceId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Province> provMap = provinceRepository.findAllByIdInAndDeletedFalse(provIds)
                .stream().collect(Collectors.toMap(Province::getId, Function.identity()));

        return wards.stream().map(w -> {
            WardResponse res = WardResponse.builder()
                    .id(w.getId())
                    .wardCode(w.getWardCode())
                    .wardName(w.getWardName())
                    .wardType(w.getWardType())
                    .districtId(w.getDistrictId())
                    .build();
            if (w.getDistrictId() != null && distMap.containsKey(w.getDistrictId())) {
                District d = distMap.get(w.getDistrictId());
                res.setDistrictName(d.getDistrictName());
                res.setProvinceId(d.getProvinceId());
                if (d.getProvinceId() != null && provMap.containsKey(d.getProvinceId())) {
                    res.setProvinceName(provMap.get(d.getProvinceId()).getProvinceName());
                }
            }
            return res;
        }).toList();
    }
}
