package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.EnrollmentRequest;
import com.toan.university_management.dto.response.masterdata.EnrollmentResponse;
import com.toan.university_management.entity.masterdata.Enrollment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "totalScore", ignore = true)
    @Mapping(target = "enrolledAt", ignore = true)
    Enrollment toEnrollment(EnrollmentRequest request);

    EnrollmentResponse toEnrollmentResponse(Enrollment enrollment);

    List<EnrollmentResponse> toEnrollmentResponseList(List<Enrollment> list);

    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "totalScore", ignore = true)
    @Mapping(target = "enrolledAt", ignore = true)
    void updateEnrollment(@MappingTarget Enrollment enrollment, EnrollmentRequest request);
}
