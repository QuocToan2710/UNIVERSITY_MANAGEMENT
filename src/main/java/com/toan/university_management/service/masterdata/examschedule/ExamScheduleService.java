package com.toan.university_management.service.masterdata.examschedule;

import com.toan.university_management.model.masterdata.ExamScheduleRequest;
import com.toan.university_management.model.masterdata.ExamScheduleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamScheduleService {
    ExamScheduleResponse createExamSchedule(ExamScheduleRequest request);
    ExamScheduleResponse getExamScheduleById(Long id);
    List<ExamScheduleResponse> getAllExamSchedules();
    Page<ExamScheduleResponse> getAllExamSchedules(Pageable pageable);
    List<ExamScheduleResponse> getMyExamSchedules();
    ExamScheduleResponse updateExamSchedule(Long id, ExamScheduleRequest request);
    void deleteExamSchedule(Long id);
}
