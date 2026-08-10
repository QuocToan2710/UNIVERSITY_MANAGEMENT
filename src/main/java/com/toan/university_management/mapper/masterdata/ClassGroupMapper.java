package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.ClassGroupRequest;
import com.toan.university_management.dto.response.masterdata.ClassGroupResponse;
import com.toan.university_management.entity.masterdata.ClassGroup;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassGroupMapper {

    @Mapping(target = "homeroomTeacher", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    ClassGroup toClassGroup(ClassGroupRequest request);

    @Mapping(source = "homeroomTeacher.id", target = "homeroomTeacherId")
    @Mapping(source = "homeroomTeacher.fullName", target = "homeroomTeacherName")
    @Mapping(expression = "java(classGroup.getStudents() != null ? classGroup.getStudents().size() : 0)",
             target = "studentCount")
    ClassGroupResponse toClassGroupResponse(ClassGroup classGroup);

    List<ClassGroupResponse> toClassGroupResponseList(List<ClassGroup> list);

    @Mapping(target = "homeroomTeacher", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateClassGroup(@MappingTarget ClassGroup classGroup, ClassGroupRequest request);
}


