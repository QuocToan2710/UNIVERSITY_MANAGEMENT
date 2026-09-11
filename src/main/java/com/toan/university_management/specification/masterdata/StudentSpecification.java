package com.toan.university_management.specification.masterdata;

import com.toan.university_management.common.specification.BaseSpecification;
import com.toan.university_management.dto.request.masterdata.StudentSearchPaginationRQ;
import com.toan.university_management.entity.masterdata.Student;
import org.springframework.data.jpa.domain.Specification;

public final class StudentSpecification {

    private StudentSpecification() {
    }

    public static Specification<Student> filter(StudentSearchPaginationRQ request) {
        Specification<Student> spec = BaseSpecification.isNotDeleted();

        if (request == null) {
            return spec;
        }

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            spec = spec.and(BaseSpecification.keywordSearch(
                    request.getKeyword(),
                    "studentCode", "fullName", "email", "phoneNumber", "address", "specificAddress"
            ));
        }

        if (request.getStudentCode() != null && !request.getStudentCode().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("studentCode", request.getStudentCode()));
        }

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("fullName", request.getFullName()));
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            spec = spec.and(BaseSpecification.likeIgnoreCase("email", request.getEmail()));
        }

        if (request.getMajorId() != null && request.getMajorId() > 0) {
            spec = spec.and(BaseSpecification.equalsIfNotNull("majorId", request.getMajorId()));
        }

        if (request.getClassGroupId() != null && request.getClassGroupId() > 0) {
            spec = spec.and(BaseSpecification.equalsIfNotNull("classGroupId", request.getClassGroupId()));
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
