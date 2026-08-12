package com.toan.university_management.service.masterdata.subjectclass;

import com.toan.university_management.dto.request.masterdata.SubjectClassRequest;
import com.toan.university_management.dto.response.masterdata.SubjectClassResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubjectClassService {
    SubjectClassResponse createSubjectClass(SubjectClassRequest request);
    SubjectClassResponse updateSubjectClass(Long id, SubjectClassRequest request);
    void deleteSubjectClass(Long id);
    SubjectClassResponse getSubjectClassById(Long id);
    Page<SubjectClassResponse> getAllSubjectClasses(Pageable pageable);
    List<SubjectClassResponse> getAllSubjectClasses();
}
