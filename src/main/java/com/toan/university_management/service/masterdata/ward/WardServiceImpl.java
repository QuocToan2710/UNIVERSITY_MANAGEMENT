package com.toan.university_management.service.masterdata.ward;

import com.toan.university_management.dto.request.masterdata.WardRequest;
import com.toan.university_management.dto.request.masterdata.WardSearchPaginationRQ;
import com.toan.university_management.dto.response.BasePaginationRS;
import com.toan.university_management.dto.response.masterdata.WardResponse;
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

        String kw = search.getKeyword() != null ? search.getKeyword().trim().toLowerCase() : "";
        String code = search.getWardCode() != null ? search.getWardCode().trim().toLowerCase() : "";
        String name = search.getWardName() != null ? search.getWardName().trim().toLowerCase() : "";
        String type = search.getWardType() != null ? search.getWardType().trim().toLowerCase() : "";
        Long districtId = search.getDistrictId();
        Long provinceId = search.getProvinceId();

        List<WardResponse> all = getAllWards(null).stream()
                .filter(w -> {
                    if (districtId != null && !Objects.equals(w.getDistrictId(), districtId)) return false;
                    if (provinceId != null && !Objects.equals(w.getProvinceId(), provinceId)) return false;
                    if (!kw.isEmpty()) {
                        String full = ((w.getWardCode() != null ? w.getWardCode() : "") + " "
                                + (w.getWardName() != null ? w.getWardName() : "") + " "
                                + (w.getWardType() != null ? w.getWardType() : "") + " "
                                + (w.getDistrictName() != null ? w.getDistrictName() : "") + " "
                                + (w.getProvinceName() != null ? w.getProvinceName() : "")).toLowerCase();
                        if (!full.contains(kw)) return false;
                    }
                    if (!code.isEmpty() && (w.getWardCode() == null || !w.getWardCode().toLowerCase().contains(code))) return false;
                    if (!name.isEmpty() && (w.getWardName() == null || !w.getWardName().toLowerCase().contains(name))) return false;
                    if (!type.isEmpty() && (w.getWardType() == null || !w.getWardType().toLowerCase().contains(type))) return false;
                    return true;
                })
                .toList();

        int total = all.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);

        return BasePaginationRS.<WardResponse>builder()
                .items(all.subList(from, to))
                .totalCount(total)
                .totalPage((int) Math.ceil((double) total / size))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WardResponse> export(WardSearchPaginationRQ search) {
        WardSearchPaginationRQ copy = search != null
                ? WardSearchPaginationRQ.builder()
                .keyword(search.getKeyword())
                .wardCode(search.getWardCode())
                .wardName(search.getWardName())
                .wardType(search.getWardType())
                .districtId(search.getDistrictId())
                .provinceId(search.getProvinceId())
                .pageNumber(0)
                .pageSize(Integer.MAX_VALUE)
                .build()
                : WardSearchPaginationRQ.builder().pageNumber(0).pageSize(Integer.MAX_VALUE).build();
        return search(copy).getItems();
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
