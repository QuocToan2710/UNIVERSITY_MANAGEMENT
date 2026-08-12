package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.SubjectClassRequest;
import com.toan.university_management.dto.response.masterdata.SubjectClassResponse;
import com.toan.university_management.entity.masterdata.SubjectClass;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectClassMapper {
    @Mapping(target = "deleted", ignore = true)
    SubjectClass toSubjectClass(SubjectClassRequest request);

    SubjectClassResponse toSubjectClassResponse(SubjectClass subjectClass);

    List<SubjectClassResponse> toSubjectClassResponseList(List<SubjectClass> list);

    @Mapping(target = "deleted", ignore = true)
    void updateSubjectClass(@MappingTarget SubjectClass subjectClass, SubjectClassRequest request);
}
