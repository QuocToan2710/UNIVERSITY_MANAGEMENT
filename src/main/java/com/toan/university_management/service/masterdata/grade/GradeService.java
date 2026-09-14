package com.toan.university_management.service.masterdata.grade;

import com.toan.university_management.model.masterdata.GradeBatchUpdateRequest;
import com.toan.university_management.model.masterdata.StudentTranscriptResponse;
import com.toan.university_management.model.masterdata.SubjectClassGradeSummaryResponse;

public interface GradeService {

    SubjectClassGradeSummaryResponse getSubjectClassGrades(Long subjectClassId);

    SubjectClassGradeSummaryResponse updateBatchGrades(Long subjectClassId, GradeBatchUpdateRequest request);

    SubjectClassGradeSummaryResponse submitGrades(Long subjectClassId);

    SubjectClassGradeSummaryResponse publishGrades(Long subjectClassId);

    SubjectClassGradeSummaryResponse lockGrades(Long subjectClassId);

    StudentTranscriptResponse getStudentTranscript(Long studentId);

    StudentTranscriptResponse getMyTranscript();
}
