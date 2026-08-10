package com.toan.university_management.mapper.masterdata;


import com.toan.university_management.dto.request.masterdata.TeacherRequest;
import com.toan.university_management.dto.response.masterdata.CourseResponse;
import com.toan.university_management.dto.response.masterdata.TeacherResponse;
import com.toan.university_management.entity.masterdata.Course;
import com.toan.university_management.entity.masterdata.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    Teacher toTeacher(TeacherRequest request);
    TeacherResponse toTeacherResponse(Teacher teacher);
    List<TeacherResponse> toTeacherResponseList(List<Teacher> teachers);

    @Mapping(target = "courses", ignore = true)
    void updateTeacher(@MappingTarget Teacher teacher, TeacherRequest request);
}


