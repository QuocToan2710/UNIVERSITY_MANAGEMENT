package com.toan.university_management.service;

import com.toan.university_management.dto.request.TeacherRequest;
import com.toan.university_management.dto.response.TeacherResponse;

import java.util.List;

public interface TeacherService {
    TeacherResponse createTeacher(TeacherRequest request);
    TeacherResponse getTeacherById(String id);
    List<TeacherResponse> getAllTeachers();
    TeacherResponse updateTeacher(String id, TeacherRequest request);

    void deleteTeacher(String  id);
    List<TeacherResponse> getTeachersBySpecialization(String specialization);
}
