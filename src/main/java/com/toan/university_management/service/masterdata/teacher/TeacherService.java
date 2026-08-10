package com.toan.university_management.service.masterdata.teacher;

import com.toan.university_management.dto.request.masterdata.TeacherRequest;
import com.toan.university_management.dto.response.masterdata.TeacherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeacherService {
    TeacherResponse createTeacher(TeacherRequest request);
    TeacherResponse getTeacherById(String id);
    List<TeacherResponse> getAllTeachers();
    Page<TeacherResponse> getAllTeachers(Pageable pageable);
    TeacherResponse updateTeacher(String id, TeacherRequest request);

    void deleteTeacher(String  id);
    List<TeacherResponse> getTeachersBySpecialization(String specialization);
}


