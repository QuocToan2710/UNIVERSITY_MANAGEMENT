package com.toan.university_management.specification.masterdata;

import com.toan.university_management.common.specification.BaseSpecification;
import com.toan.university_management.model.masterdata.DistrictSearchPaginationRQ;
import com.toan.university_management.entity.masterdata.District;
import org.springframework.data.jpa.domain.Specification;

public final class DistrictSpecification {

    private DistrictSpecification() {
    }

    public static Specification<District> filter(DistrictSearchPaginationRQ request) {
        Specification<District> spec = BaseSpecification.isNotDeleted();

        if (request == null) {
            return spec;
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            spec = spec.and(BaseSpecification.keywordSearch(
                    request.getKeyword(),
                    "districtCode", "districtName", "districtType"
            ));
        }

        if (request.getDistrictCode() != null && !request.getDistrictCode().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("districtCode", request.getDistrictCode()));
        }

        if (request.getDistrictName() != null && !request.getDistrictName().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("districtName", request.getDistrictName()));
        }

        if (request.getDistrictType() != null && !request.getDistrictType().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("districtType", request.getDistrictType()));
        }

        if (request.getProvinceId() != null && request.getProvinceId() > 0) {
            spec = spec.and(BaseSpecification.equalsIfNotNull("provinceId", request.getProvinceId()));
        }

        return spec;
    }
}
