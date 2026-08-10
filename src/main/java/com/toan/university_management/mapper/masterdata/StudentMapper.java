package com.toan.university_management.mapper.masterdata;


import com.toan.university_management.dto.request.masterdata.StudentRequest;
import com.toan.university_management.dto.response.masterdata.CourseSummary;
import com.toan.university_management.dto.response.masterdata.StudentResponse;
import com.toan.university_management.entity.masterdata.Course;
import com.toan.university_management.entity.masterdata.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "classGroup", ignore = true)
    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "status", ignore = true)
    Student toStudent(StudentRequest request);

    @Mapping(source = "classGroup.id", target = "classGroupId")
    @Mapping(source = "classGroup.className", target = "classGroupName")
    @Mapping(source = "status", target = "status", defaultExpression = "java(\"ACTIVE\")")
    StudentResponse toStudentResponse(Student student);

    @Mapping(target = "classGroup", ignore = true)
    @Mapping(target = "courses", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateStudent(@MappingTarget Student student, StudentRequest request);

    List<StudentResponse> toStudentResponseList(List<Student> students);
}



