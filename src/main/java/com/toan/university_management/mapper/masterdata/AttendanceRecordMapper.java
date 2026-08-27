package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.dto.request.masterdata.AttendanceRecordItemRequest;
import com.toan.university_management.dto.response.masterdata.AttendanceRecordResponse;
import com.toan.university_management.entity.masterdata.AttendanceRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AttendanceRecordMapper {
    AttendanceRecord toAttendanceRecord(AttendanceRecordItemRequest request);
    AttendanceRecordResponse toAttendanceRecordResponse(AttendanceRecord record);
    void updateAttendanceRecord(@MappingTarget AttendanceRecord record, AttendanceRecordItemRequest request);
}
