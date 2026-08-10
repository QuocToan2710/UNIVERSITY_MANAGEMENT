package com.toan.university_management.mapper.masterdata;


import com.toan.university_management.dto.request.masterdata.CourseRequest;
import com.toan.university_management.dto.response.masterdata.CourseResponse;
import com.toan.university_management.entity.masterdata.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TeacherMapper.class, StudentMapper.class})
public interface CourseMapper {
    Course toCourse(CourseRequest request);
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherCode", source = "teacher.teacherCode")
    @Mapping(target = "teacherName", source = "teacher.fullName")
    @Mapping(target = "teacherEmail", source = "teacher.email")
    @Mapping(target = "teacherPhone", source = "teacher.phoneNumber")
    @Mapping(target = "teacherSpecialization", source = "teacher.specialization")
    @Mapping(target = "students", source = "students")
    CourseResponse toCourseResponse(Course course);
    List<CourseResponse> toCourseResponseList(List<Course> courses);

    void updateCourse(@MappingTarget Course course, CourseRequest request);
}


