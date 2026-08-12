package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.StudentRequest;
import com.toan.university_management.dto.response.masterdata.StudentResponse;
import com.toan.university_management.entity.masterdata.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "deleted", ignore = true)
    Student toStudent(StudentRequest request);

    StudentResponse toStudentResponse(Student student);

    @Mapping(target = "deleted", ignore = true)
    void updateStudent(@MappingTarget Student student, StudentRequest request);

    List<StudentResponse> toStudentResponseList(List<Student> students);
}
