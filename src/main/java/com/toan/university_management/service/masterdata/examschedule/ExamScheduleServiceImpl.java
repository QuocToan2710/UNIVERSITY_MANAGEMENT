package com.toan.university_management.service.masterdata.examschedule;

import com.toan.university_management.dto.request.masterdata.ExamScheduleRequest;
import com.toan.university_management.dto.response.masterdata.ExamScheduleResponse;
import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.ExamSchedule;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.ExamScheduleMapper;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.masterdata.EnrollmentRepository;
import com.toan.university_management.repository.masterdata.ExamScheduleRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.SubjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
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
    UserRepository userRepository;
    StudentRepository studentRepository;
    EnrollmentRepository enrollmentRepository;

    @Override
    public ExamScheduleResponse createExamSchedule(ExamScheduleRequest request) {
        if (examScheduleRepository.existsByExamCodeAndDeletedFalse(request.getExamCode())) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        }
        ExamSchedule examSchedule = examScheduleMapper.toExamSchedule(request);
        examSchedule = examScheduleRepository.save(examSchedule);
        return enrichResponse(examSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamScheduleResponse getExamScheduleById(Long id) {
        ExamSchedule examSchedule = examScheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        return enrichResponse(examSchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamScheduleResponse> getAllExamSchedules() {
        return enrichResponses(examScheduleRepository.findAllByDeletedFalse());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExamScheduleResponse> getAllExamSchedules(Pageable pageable) {
        Page<ExamSchedule> page = examScheduleRepository.findAllByDeletedFalse(pageable);
        List<ExamScheduleResponse> content = enrichResponses(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamScheduleResponse> getMyExamSchedules() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByUsernameIgnoreCase(username))
                .or(() -> userRepository.findByEmail(username))
                .flatMap(u -> studentRepository.findByUserIdAndDeletedFalse(u.getId())
                        .or(() -> studentRepository.findByStudentCodeAndDeletedFalse(u.getUsername()))
                        .or(() -> (u.getUserCode() != null && !u.getUserCode().isBlank()) ? studentRepository.findByStudentCodeAndDeletedFalse(u.getUserCode()) : Optional.empty())
                        .or(() -> (u.getEmail() != null && !u.getEmail().isBlank()) ? studentRepository.findByEmailAndDeletedFalse(u.getEmail()) : Optional.empty()))
                .map(student -> {
                    Set<Long> subjectClassIds = enrollmentRepository
                            .findAllByStudentIdAndDeletedFalse(student.getId())
                            .stream()
                            .map(Enrollment::getSubjectClassId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());
                    if (subjectClassIds.isEmpty()) return Collections.<ExamScheduleResponse>emptyList();
                    return enrichResponses(
                            examScheduleRepository.findAllBySubjectClassIdInAndDeletedFalse(subjectClassIds));
                })
                .orElse(Collections.emptyList());
    }

    @Override
    public ExamScheduleResponse updateExamSchedule(Long id, ExamScheduleRequest request) {
        ExamSchedule examSchedule = examScheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        examScheduleMapper.updateExamSchedule(examSchedule, request);
        examSchedule = examScheduleRepository.save(examSchedule);
        return enrichResponse(examSchedule);
    }

    @Override
    public void deleteExamSchedule(Long id) {
        ExamSchedule examSchedule = examScheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        examSchedule.setDeleted(true);
        examScheduleRepository.save(examSchedule);
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
