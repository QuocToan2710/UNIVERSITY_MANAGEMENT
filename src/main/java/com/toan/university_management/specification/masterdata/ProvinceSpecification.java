package com.toan.university_management.specification.masterdata;

import com.toan.university_management.common.specification.BaseSpecification;
import com.toan.university_management.model.masterdata.ProvinceSearchPaginationRQ;
import com.toan.university_management.entity.masterdata.Province;
import org.springframework.data.jpa.domain.Specification;

public final class ProvinceSpecification {

    private ProvinceSpecification() {
    }

    public static Specification<Province> filter(ProvinceSearchPaginationRQ request) {
        Specification<Province> spec = BaseSpecification.isNotDeleted();

        if (request == null) {
            return spec;
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            spec = spec.and(BaseSpecification.keywordSearch(
                    request.getKeyword(),
                    "provinceCode", "provinceName", "provinceType"
            ));
        }

        if (request.getProvinceCode() != null && !request.getProvinceCode().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("provinceCode", request.getProvinceCode()));
        }

        if (request.getProvinceName() != null && !request.getProvinceName().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("provinceName", request.getProvinceName()));
        }

        if (request.getProvinceType() != null && !request.getProvinceType().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("provinceType", request.getProvinceType()));
        }

        return spec;
    }
}
