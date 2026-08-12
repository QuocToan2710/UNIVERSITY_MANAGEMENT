package com.toan.university_management.service.masterdata.major;

import com.toan.university_management.dto.request.masterdata.MajorRequest;
import com.toan.university_management.dto.response.masterdata.MajorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MajorService {
    MajorResponse createMajor(MajorRequest request);
    MajorResponse getMajorById(Long id);
    List<MajorResponse> getAllMajors();
    Page<MajorResponse> getAllMajors(Pageable pageable);
    MajorResponse updateMajor(Long id, MajorRequest request);
    void deleteMajor(Long id);
}
