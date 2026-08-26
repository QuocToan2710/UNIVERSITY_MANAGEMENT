package com.toan.university_management.service.masterdata.enrollment;

import com.toan.university_management.dto.request.masterdata.BatchEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.ClassGroupEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.EnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.StudentRegistrationRequest;
import com.toan.university_management.dto.response.masterdata.AvailableSubjectClassResponse;
import com.toan.university_management.dto.response.masterdata.BatchEnrollmentResultResponse;
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

    EnrollmentResponse registerStudent(StudentRegistrationRequest request);
    void cancelRegistration(Long subjectClassId);
    void cancelRegistrationById(Long enrollmentId);
    List<EnrollmentResponse> getMyRegistrations(String semester, String academicYear);
    List<EnrollmentResponse> getEnrollmentsBySubjectClass(Long subjectClassId);
    List<AvailableSubjectClassResponse> getAvailableClassesForRegistration(String semester, String academicYear);
    BatchEnrollmentResultResponse batchEnroll(BatchEnrollmentRequest request);
    BatchEnrollmentResultResponse enrollClassGroup(ClassGroupEnrollmentRequest request);
}

