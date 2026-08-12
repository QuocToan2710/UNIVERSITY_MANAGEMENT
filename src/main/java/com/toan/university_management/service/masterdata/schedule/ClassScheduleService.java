package com.toan.university_management.service.masterdata.schedule;

import com.toan.university_management.dto.request.masterdata.ClassScheduleRequest;
import com.toan.university_management.dto.response.masterdata.ClassScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassScheduleService {
    ClassScheduleResponse createSchedule(ClassScheduleRequest request);
    ClassScheduleResponse updateSchedule(Long id, ClassScheduleRequest request);
    void deleteSchedule(Long id);
    ClassScheduleResponse getScheduleById(Long id);
    Page<ClassScheduleResponse> getAllSchedules(Pageable pageable);
    List<ClassScheduleResponse> getAllSchedules();
    List<ClassScheduleResponse> getMySchedule(String semester, String academicYear);
    List<ClassScheduleResponse> getByTeacher(Long teacherId, String semester, String academicYear);
    List<ClassScheduleResponse> getByClassGroup(Long classGroupId, String semester, String academicYear);
    List<ClassScheduleResponse> getBySubject(Long subjectId, String semester, String academicYear);
    List<ClassScheduleResponse> getByStudent(Long studentId, String semester, String academicYear);
}
