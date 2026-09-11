package com.toan.university_management.specification.masterdata;

import com.toan.university_management.common.specification.BaseSpecification;
import com.toan.university_management.dto.request.masterdata.WardSearchPaginationRQ;
import com.toan.university_management.entity.masterdata.Ward;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public final class WardSpecification {

    private WardSpecification() {
    }

    public static Specification<Ward> filter(WardSearchPaginationRQ request) {
        return filter(request, null);
    }

    public static Specification<Ward> filter(WardSearchPaginationRQ request, Collection<Long> matchedDistrictIds) {
        Specification<Ward> spec = BaseSpecification.isNotDeleted();

        if (request == null) {
            return spec;
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            spec = spec.and(BaseSpecification.keywordSearch(
                    request.getKeyword(),
                    "wardCode", "wardName", "wardType"
            ));
        }

        if (request.getWardCode() != null && !request.getWardCode().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("wardCode", request.getWardCode()));
        }

        if (request.getWardName() != null && !request.getWardName().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("wardName", request.getWardName()));
        }

        if (request.getWardType() != null && !request.getWardType().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("wardType", request.getWardType()));
        }

        if (request.getDistrictId() != null && request.getDistrictId() > 0) {
            spec = spec.and(BaseSpecification.equalsIfNotNull("districtId", request.getDistrictId()));
        } else if (matchedDistrictIds != null) {
            spec = spec.and(BaseSpecification.inIfNotEmpty("districtId", matchedDistrictIds));
        }

        return spec;
    }
}
