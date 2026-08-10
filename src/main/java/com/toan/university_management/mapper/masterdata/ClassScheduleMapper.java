package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.ClassScheduleRequest;
import com.toan.university_management.dto.response.masterdata.ClassScheduleResponse;
import com.toan.university_management.entity.masterdata.ClassSchedule;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassScheduleMapper {

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "classGroup", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    ClassSchedule toClassSchedule(ClassScheduleRequest request);

    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "course.courseName", target = "courseName")
    @Mapping(source = "course.courseCode", target = "courseCode")
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "teacher.fullName", target = "teacherName")
    @Mapping(source = "teacher.teacherCode", target = "teacherCode")
    @Mapping(source = "classGroup.id", target = "classGroupId")
    @Mapping(source = "classGroup.className", target = "classGroupName")
    @Mapping(source = "classGroup.classCode", target = "classGroupCode")
    ClassScheduleResponse toClassScheduleResponse(ClassSchedule schedule);

    List<ClassScheduleResponse> toClassScheduleResponseList(List<ClassSchedule> list);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "classGroup", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateClassSchedule(@MappingTarget ClassSchedule schedule, ClassScheduleRequest request);
}


