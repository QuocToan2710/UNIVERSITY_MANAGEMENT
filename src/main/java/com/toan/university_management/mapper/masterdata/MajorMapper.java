package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.MajorRequest;
import com.toan.university_management.dto.response.masterdata.MajorResponse;
import com.toan.university_management.entity.masterdata.Major;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MajorMapper {
    @Mapping(target = "deleted", ignore = true)
    Major toMajor(MajorRequest request);

    MajorResponse toMajorResponse(Major major);

    List<MajorResponse> toMajorResponseList(List<Major> list);

    @Mapping(target = "deleted", ignore = true)
    void updateMajor(@MappingTarget Major major, MajorRequest request);
}
