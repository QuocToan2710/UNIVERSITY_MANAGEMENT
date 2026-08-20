package com.toan.university_management.service.masterdata.province;

import com.toan.university_management.dto.request.masterdata.ProvinceRequest;
import com.toan.university_management.dto.request.masterdata.ProvinceSearchPaginationRQ;
import com.toan.university_management.dto.response.BasePaginationRS;
import com.toan.university_management.dto.response.masterdata.ProvinceResponse;
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

        String kw = search.getKeyword() != null ? search.getKeyword().trim().toLowerCase() : "";
        String code = search.getProvinceCode() != null ? search.getProvinceCode().trim().toLowerCase() : "";
        String name = search.getProvinceName() != null ? search.getProvinceName().trim().toLowerCase() : "";
        String type = search.getProvinceType() != null ? search.getProvinceType().trim().toLowerCase() : "";

        List<ProvinceResponse> all = provinceRepository.findAllByDeletedFalseOrderByProvinceNameAsc().stream()
                .map(this::toResponse)
                .filter(p -> {
                    if (!kw.isEmpty()) {
                        String full = ((p.getProvinceCode() != null ? p.getProvinceCode() : "") + " "
                                + (p.getProvinceName() != null ? p.getProvinceName() : "") + " "
                                + (p.getProvinceType() != null ? p.getProvinceType() : "")).toLowerCase();
                        if (!full.contains(kw)) return false;
                    }
                    if (!code.isEmpty() && (p.getProvinceCode() == null || !p.getProvinceCode().toLowerCase().contains(code))) return false;
                    if (!name.isEmpty() && (p.getProvinceName() == null || !p.getProvinceName().toLowerCase().contains(name))) return false;
                    if (!type.isEmpty() && (p.getProvinceType() == null || !p.getProvinceType().toLowerCase().contains(type))) return false;
                    return true;
                })
                .toList();

        int total = all.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);

        return BasePaginationRS.<ProvinceResponse>builder()
                .items(all.subList(from, to))
                .totalCount(total)
                .totalPage((int) Math.ceil((double) total / size))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProvinceResponse> export(ProvinceSearchPaginationRQ search) {
        ProvinceSearchPaginationRQ copy = search != null
                ? ProvinceSearchPaginationRQ.builder()
                .keyword(search.getKeyword())
                .provinceCode(search.getProvinceCode())
                .provinceName(search.getProvinceName())
                .provinceType(search.getProvinceType())
                .pageNumber(0)
                .pageSize(Integer.MAX_VALUE)
                .build()
                : ProvinceSearchPaginationRQ.builder().pageNumber(0).pageSize(Integer.MAX_VALUE).build();
        return search(copy).getItems();
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
