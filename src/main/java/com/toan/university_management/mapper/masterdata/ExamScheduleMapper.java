package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.model.masterdata.ExamScheduleRequest;
import com.toan.university_management.model.masterdata.ExamScheduleResponse;
import com.toan.university_management.entity.masterdata.ExamSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExamScheduleMapper {
    ExamSchedule toExamSchedule(ExamScheduleRequest request);
    ExamScheduleResponse toExamScheduleResponse(ExamSchedule examSchedule);
    void updateExamSchedule(@MappingTarget ExamSchedule examSchedule, ExamScheduleRequest request);
}
