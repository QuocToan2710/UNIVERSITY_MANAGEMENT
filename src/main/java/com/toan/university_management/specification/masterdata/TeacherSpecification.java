package com.toan.university_management.specification.masterdata;

import com.toan.university_management.common.specification.BaseSpecification;
import com.toan.university_management.model.masterdata.TeacherSearchPaginationRQ;
import com.toan.university_management.entity.masterdata.Teacher;
import org.springframework.data.jpa.domain.Specification;

public final class TeacherSpecification {

    private TeacherSpecification() {
    }

    public static Specification<Teacher> filter(TeacherSearchPaginationRQ request) {
        Specification<Teacher> spec = BaseSpecification.isNotDeleted();

        if (request == null) {
            return spec;
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            spec = spec.and(BaseSpecification.keywordSearch(
                    request.getKeyword(),
                    "teacherCode", "fullName", "email", "phoneNumber", "degree", "address", "specificAddress"
            ));
        }

        if (request.getTeacherCode() != null && !request.getTeacherCode().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("teacherCode", request.getTeacherCode()));
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("fullName", request.getFullName()));
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("email", request.getEmail()));
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("phoneNumber", request.getPhoneNumber()));
        }

        if (request.getDegree() != null && !request.getDegree().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("degree", request.getDegree()));
        }

        if (request.getDepartmentId() != null && request.getDepartmentId() > 0) {
            spec = spec.and(BaseSpecification.equalsIfNotNull("departmentId", request.getDepartmentId()));
        }

        if (request.getProvinceId() != null && request.getProvinceId() > 0) {
            spec = spec.and(BaseSpecification.equalsIfNotNull("provinceId", request.getProvinceId()));
        }

        if (request.getDistrictId() != null && request.getDistrictId() > 0) {
            spec = spec.and(BaseSpecification.equalsIfNotNull("districtId", request.getDistrictId()));
        }

        if (request.getWardId() != null && request.getWardId() > 0) {
            spec = spec.and(BaseSpecification.equalsIfNotNull("wardId", request.getWardId()));
        }

        return spec;
    }
}
