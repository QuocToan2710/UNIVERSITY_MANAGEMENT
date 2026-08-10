package com.toan.university_management.service.masterdata.schedule;

import com.toan.university_management.dto.request.masterdata.ClassScheduleRequest;
import com.toan.university_management.dto.response.masterdata.ClassScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClassScheduleService {
    ClassScheduleResponse createSchedule(ClassScheduleRequest request);
    ClassScheduleResponse updateSchedule(String id, ClassScheduleRequest request);
    void deleteSchedule(String id);
    ClassScheduleResponse getScheduleById(String id);
    Page<ClassScheduleResponse> getAllSchedules(Pageable pageable);
    List<ClassScheduleResponse> getByTeacher(String teacherId, String semester, String academicYear);
    List<ClassScheduleResponse> getByClassGroup(String classGroupId, String semester, String academicYear);
    List<ClassScheduleResponse> getByCourse(String courseId, String semester, String academicYear);
    List<ClassScheduleResponse> getByStudent(String studentId, String semester, String academicYear);
    List<ClassScheduleResponse> getMySchedule(String semester, String academicYear);
}



