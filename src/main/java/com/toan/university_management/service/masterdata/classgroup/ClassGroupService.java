package com.toan.university_management.service.masterdata.classgroup;

import com.toan.university_management.dto.request.masterdata.ClassGroupRequest;
import com.toan.university_management.dto.response.masterdata.ClassGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassGroupService {
    ClassGroupResponse createClassGroup(ClassGroupRequest request);
    ClassGroupResponse updateClassGroup(String id, ClassGroupRequest request);
    void deleteClassGroup(String id);
    ClassGroupResponse getClassGroupById(String id);
    Page<ClassGroupResponse> getAllClassGroups(Pageable pageable);
    List<ClassGroupResponse> getAllClassGroups();
}


