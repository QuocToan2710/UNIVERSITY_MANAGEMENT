package com.toan.university_management.service;

import com.toan.university_management.dto.request.StudentRequest;
import com.toan.university_management.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);
    StudentResponse getStudentById(String id);
    List<StudentResponse> getAllStudents();
    StudentResponse updateStudent(String  id, StudentRequest request);
    void deleteStudent(String  id);
}
