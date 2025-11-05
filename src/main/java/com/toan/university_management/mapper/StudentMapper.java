package com.toan.university_management.mapper;


import com.toan.university_management.dto.request.StudentRequest;
import com.toan.university_management.dto.response.CourseSummary;
import com.toan.university_management.dto.response.StudentResponse;
import com.toan.university_management.entity.Course;
import com.toan.university_management.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    Student toStudent(StudentRequest request);

    StudentResponse toStudentResponse(Student student);

    void updateStudent(@MappingTarget Student student, StudentRequest request);

    List<StudentResponse> toStudentResponseList(List<Student> students);
}
