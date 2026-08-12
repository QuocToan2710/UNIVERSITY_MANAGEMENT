package com.toan.university_management.service.masterdata.subject;

import com.toan.university_management.dto.request.masterdata.SubjectRequest;
import com.toan.university_management.dto.response.masterdata.SubjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubjectService {
    SubjectResponse createSubject(SubjectRequest request);
    SubjectResponse getSubjectById(Long id);
    List<SubjectResponse> getAllSubjects();
    Page<SubjectResponse> getAllSubjects(Pageable pageable);
    SubjectResponse updateSubject(Long id, SubjectRequest request);
    void deleteSubject(Long id);
}
