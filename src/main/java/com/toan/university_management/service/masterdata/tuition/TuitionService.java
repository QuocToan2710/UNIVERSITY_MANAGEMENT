package com.toan.university_management.service.masterdata.tuition;

import com.toan.university_management.dto.request.masterdata.RecordPaymentRequest;
import com.toan.university_management.dto.response.masterdata.StudentTuitionSummaryResponse;
import com.toan.university_management.dto.response.masterdata.TuitionDashboardSummaryResponse;
import com.toan.university_management.enums.TuitionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TuitionService {

    StudentTuitionSummaryResponse getMyTuitionSummary(String semester, String academicYear);

    List<StudentTuitionSummaryResponse> getMyTuitionHistory();

    StudentTuitionSummaryResponse getStudentTuitionSummary(Long studentId, String semester, String academicYear);

    Page<StudentTuitionSummaryResponse> getAllStudentsTuition(
            String semester,
            String academicYear,
            Long classGroupId,
            TuitionStatus status,
            String search,
            Pageable pageable
    );

    TuitionDashboardSummaryResponse getDashboardSummary(String semester, String academicYear);

    StudentTuitionSummaryResponse recordPayment(RecordPaymentRequest request);
}