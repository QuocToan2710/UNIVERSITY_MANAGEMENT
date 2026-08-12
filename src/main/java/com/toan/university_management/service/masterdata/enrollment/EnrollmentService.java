package com.toan.university_management.service.masterdata.enrollment;

import com.toan.university_management.dto.request.masterdata.EnrollmentRequest;
import com.toan.university_management.dto.response.masterdata.EnrollmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse createEnrollment(EnrollmentRequest request);
    EnrollmentResponse getEnrollmentById(Long id);
    List<EnrollmentResponse> getAllEnrollments();
    Page<EnrollmentResponse> getAllEnrollments(Pageable pageable);
    EnrollmentResponse updateEnrollment(Long id, EnrollmentRequest request);
    void deleteEnrollment(Long id);
}
