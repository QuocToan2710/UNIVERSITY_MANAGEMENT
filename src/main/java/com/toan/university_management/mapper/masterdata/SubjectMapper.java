package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.SubjectRequest;
import com.toan.university_management.dto.response.masterdata.SubjectResponse;
import com.toan.university_management.entity.masterdata.Subject;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    @Mapping(target = "deleted", ignore = true)
    Subject toSubject(SubjectRequest request);

    SubjectResponse toSubjectResponse(Subject subject);

    List<SubjectResponse> toSubjectResponseList(List<Subject> list);

    @Mapping(target = "deleted", ignore = true)
    void updateSubject(@MappingTarget Subject subject, SubjectRequest request);
}
