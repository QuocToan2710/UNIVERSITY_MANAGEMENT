package com.toan.university_management.service.masterdata.examschedule;

import com.toan.university_management.dto.request.masterdata.ExamScheduleRequest;
import com.toan.university_management.dto.response.masterdata.ExamScheduleResponse;
import com.toan.university_management.entity.masterdata.ExamSchedule;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.ExamScheduleMapper;
import com.toan.university_management.repository.masterdata.ExamScheduleRepository;
import com.toan.university_management.repository.masterdata.SubjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class ExamScheduleServiceImpl implements ExamScheduleService {
    ExamScheduleRepository examScheduleRepository;
    SubjectRepository subjectRepository;
    ExamScheduleMapper examScheduleMapper;

    @Override
    public ExamScheduleResponse createExamSchedule(ExamScheduleRequest request) {
        if (examScheduleRepository.existsByExamCodeAndDeletedFalse(request.getExamCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        ExamSchedule examSchedule = examScheduleMapper.toExamSchedule(request);
        examSchedule = examScheduleRepository.save(examSchedule);
        return enrichResponse(examSchedule);
    }

    @Override
    public ExamScheduleResponse getExamScheduleById(Long id) {
        ExamSchedule examSchedule = examScheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return enrichResponse(examSchedule);
    }

    @Override
    public List<ExamScheduleResponse> getAllExamSchedules() {
        return enrichResponses(examScheduleRepository.findAllByDeletedFalse());
    }

    @Override
    public Page<ExamScheduleResponse> getAllExamSchedules(Pageable pageable) {
        Page<ExamSchedule> page = examScheduleRepository.findAllByDeletedFalse(pageable);
        List<ExamScheduleResponse> content = enrichResponses(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public List<ExamScheduleResponse> getMyExamSchedules() {
        return getAllExamSchedules();
    }

    @Override
    public ExamScheduleResponse updateExamSchedule(Long id, ExamScheduleRequest request) {
        ExamSchedule examSchedule = examScheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        examScheduleMapper.updateExamSchedule(examSchedule, request);
        examSchedule = examScheduleRepository.save(examSchedule);
        return enrichResponse(examSchedule);
    }

    @Override
    public void deleteExamSchedule(Long id) {
        if (!examScheduleRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        examScheduleRepository.deleteById(id);
    }

    private ExamScheduleResponse enrichResponse(ExamSchedule item) {
        ExamScheduleResponse res = examScheduleMapper.toExamScheduleResponse(item);
        if (item.getSubjectId() != null) {
            subjectRepository.findByIdAndDeletedFalse(item.getSubjectId()).ifPresent(s -> {
                res.setSubjectName(s.getName());
                res.setSubjectCode(s.getSubjectCode());
            });
        }
        return res;
    }

    private List<ExamScheduleResponse> enrichResponses(List<ExamSchedule> items) {
        if (items.isEmpty()) return Collections.emptyList();

        Set<Long> subjectIds = items.stream().map(ExamSchedule::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Subject> subjectMap = subjectRepository.findAllById(subjectIds)
                .stream().collect(Collectors.toMap(Subject::getId, Function.identity()));

        return items.stream().map(item -> {
            ExamScheduleResponse res = examScheduleMapper.toExamScheduleResponse(item);
            if (item.getSubjectId() != null && subjectMap.containsKey(item.getSubjectId())) {
                Subject s = subjectMap.get(item.getSubjectId());
                res.setSubjectName(s.getName());
                res.setSubjectCode(s.getSubjectCode());
            }
            return res;
        }).toList();
    }
}
