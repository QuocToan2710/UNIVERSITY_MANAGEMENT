package com.toan.university_management.service.masterdata.student;

import com.toan.university_management.model.masterdata.StudentRequest;
import com.toan.university_management.model.masterdata.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import com.toan.university_management.model.masterdata.StudentSearchPaginationRQ;
import com.toan.university_management.common.dto.BasePaginationRS;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    StudentResponse getStudentById(Long id);
    List<StudentResponse> getAllStudents();
    Page<StudentResponse> getAllStudents(Pageable pageable);
    StudentResponse updateStudent(Long id, StudentRequest request);
    void deleteStudent(Long id);
    BasePaginationRS<StudentResponse> search(StudentSearchPaginationRQ search);
    List<StudentResponse> export(StudentSearchPaginationRQ search);
}
