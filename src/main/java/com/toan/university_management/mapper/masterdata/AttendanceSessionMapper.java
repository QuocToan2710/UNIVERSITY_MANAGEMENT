package com.toan.university_management.mapper.masterdata;

import com.toan.university_management.model.masterdata.AttendanceSessionRequest;
import com.toan.university_management.model.masterdata.AttendanceSessionResponse;
import com.toan.university_management.entity.masterdata.AttendanceSession;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AttendanceSessionMapper {
    AttendanceSession toAttendanceSession(AttendanceSessionRequest request);
    AttendanceSessionResponse toAttendanceSessionResponse(AttendanceSession session);
    void updateAttendanceSession(@MappingTarget AttendanceSession session, AttendanceSessionRequest request);
}
