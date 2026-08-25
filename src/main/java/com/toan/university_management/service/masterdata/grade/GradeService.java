package com.toan.university_management.service.masterdata.grade;

import com.toan.university_management.dto.request.masterdata.GradeBatchUpdateRequest;
import com.toan.university_management.dto.response.masterdata.StudentTranscriptResponse;
import com.toan.university_management.dto.response.masterdata.SubjectClassGradeSummaryResponse;

public interface GradeService {

    SubjectClassGradeSummaryResponse getSubjectClassGrades(Long subjectClassId);

    SubjectClassGradeSummaryResponse updateBatchGrades(Long subjectClassId, GradeBatchUpdateRequest request);

    SubjectClassGradeSummaryResponse submitGrades(Long subjectClassId);

    SubjectClassGradeSummaryResponse publishGrades(Long subjectClassId);

    SubjectClassGradeSummaryResponse lockGrades(Long subjectClassId);

    StudentTranscriptResponse getStudentTranscript(Long studentId);

    StudentTranscriptResponse getMyTranscript();
}
