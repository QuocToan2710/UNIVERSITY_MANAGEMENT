package com.toan.university_management.service.masterdata.teacher;

import com.toan.university_management.dto.request.masterdata.TeacherRequest;
import com.toan.university_management.dto.response.masterdata.TeacherResponse;
import com.toan.university_management.dto.request.masterdata.TeacherSearchPaginationRQ;
import com.toan.university_management.dto.response.BasePaginationRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeacherService {
    TeacherResponse createTeacher(TeacherRequest request);
    TeacherResponse getTeacherById(Long id);
    List<TeacherResponse> getAllTeachers();
    Page<TeacherResponse> getAllTeachers(Pageable pageable);
    TeacherResponse updateTeacher(Long id, TeacherRequest request);
    void deleteTeacher(Long id);
    BasePaginationRS<TeacherResponse> search(TeacherSearchPaginationRQ search);
    List<TeacherResponse> export(TeacherSearchPaginationRQ search);
}
