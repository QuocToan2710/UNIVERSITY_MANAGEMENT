package com.toan.university_management.specification.masterdata;

import com.toan.university_management.common.specification.BaseSpecification;
import com.toan.university_management.dto.request.masterdata.BuildingSearchPaginationRQ;
import com.toan.university_management.entity.masterdata.Building;
import org.springframework.data.jpa.domain.Specification;

public final class BuildingSpecification {

    private BuildingSpecification() {
    }

    public static Specification<Building> filter(BuildingSearchPaginationRQ request) {
        Specification<Building> spec = BaseSpecification.isNotDeleted();

        if (request == null) {
            return spec;
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            spec = spec.and(BaseSpecification.keywordSearch(
                    request.getKeyword(),
                    "buildingCode", "name", "description"
            ));
        }

        if (request.getBuildingCode() != null && !request.getBuildingCode().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("buildingCode", request.getBuildingCode()));
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("name", request.getName()));
        }

        if (request.getStatus() != null && !request.getStatus().isBlank() && !"all".equalsIgnoreCase(request.getStatus())) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("status", request.getStatus()));
        }

        return spec;
    }
}
