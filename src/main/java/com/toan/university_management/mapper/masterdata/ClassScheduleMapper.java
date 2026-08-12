package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.ClassScheduleRequest;
import com.toan.university_management.dto.response.masterdata.ClassScheduleResponse;
import com.toan.university_management.entity.masterdata.ClassSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClassScheduleMapper {
    ClassSchedule toClassSchedule(ClassScheduleRequest request);
    ClassScheduleResponse toClassScheduleResponse(ClassSchedule schedule);
    void updateSchedule(@MappingTarget ClassSchedule schedule, ClassScheduleRequest request);
}
