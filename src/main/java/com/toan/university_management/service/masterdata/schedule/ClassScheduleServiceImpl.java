package com.toan.university_management.service.masterdata.schedule;

import com.toan.university_management.dto.request.masterdata.ClassScheduleRequest;
import com.toan.university_management.dto.response.masterdata.ClassScheduleResponse;
import com.toan.university_management.entity.masterdata.ClassSchedule;
import com.toan.university_management.entity.masterdata.SubjectClass;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.ClassScheduleMapper;
import com.toan.university_management.repository.masterdata.ClassScheduleRepository;
import com.toan.university_management.repository.masterdata.SubjectClassRepository;
import com.toan.university_management.repository.masterdata.TeacherRepository;
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
public class ClassScheduleServiceImpl implements ClassScheduleService {

    ClassScheduleRepository classScheduleRepository;
    SubjectClassRepository subjectClassRepository;
    TeacherRepository teacherRepository;
    ClassScheduleMapper classScheduleMapper;

    @Override
    public ClassScheduleResponse createSchedule(ClassScheduleRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime()))
            throw new AppException(ErrorCode.SCHEDULE_TIME_INVALID);

        if (!subjectClassRepository.existsByIdAndDeletedFalse(request.getSubjectClassId())) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }

        ClassSchedule schedule = classScheduleMapper.toClassSchedule(request);
        if (schedule.getScheduleCode() == null || schedule.getScheduleCode().isBlank()) {
            schedule.setScheduleCode("SCH_" + System.currentTimeMillis());
        }

        schedule = classScheduleRepository.save(schedule);
        return enrichScheduleResponse(schedule);
    }

    @Override
    public ClassScheduleResponse updateSchedule(Long id, ClassScheduleRequest request) {
        ClassSchedule schedule = classScheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (!request.getEndTime().isAfter(request.getStartTime()))
            throw new AppException(ErrorCode.SCHEDULE_TIME_INVALID);

        classScheduleMapper.updateSchedule(schedule, request);

        if (!subjectClassRepository.existsByIdAndDeletedFalse(request.getSubjectClassId())) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }

        schedule = classScheduleRepository.save(schedule);
        return enrichScheduleResponse(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        if (!classScheduleRepository.existsByIdAndDeletedFalse(id))
            throw new AppException(ErrorCode.SCHEDULE_NOT_FOUND);
        classScheduleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassScheduleResponse getScheduleById(Long id) {
        ClassSchedule schedule = classScheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));
        return enrichScheduleResponse(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassScheduleResponse> getAllSchedules(Pageable pageable) {
        Page<ClassSchedule> page = classScheduleRepository.findAllByDeletedFalse(pageable);
        List<ClassScheduleResponse> content = enrichScheduleResponses(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getAllSchedules() {
        return enrichScheduleResponses(classScheduleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getMySchedule(String semester, String academicYear) {
        return enrichScheduleResponses(classScheduleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByTeacher(Long teacherId, String semester, String academicYear) {
        return enrichScheduleResponses(classScheduleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByClassGroup(Long classGroupId, String semester, String academicYear) {
        return enrichScheduleResponses(classScheduleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getBySubject(Long subjectId, String semester, String academicYear) {
        return enrichScheduleResponses(classScheduleRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByStudent(Long studentId, String semester, String academicYear) {
        return enrichScheduleResponses(classScheduleRepository.findAll());
    }

    private ClassScheduleResponse enrichScheduleResponse(ClassSchedule schedule) {
        ClassScheduleResponse res = classScheduleMapper.toClassScheduleResponse(schedule);
        if (schedule.getSubjectClassId() != null) {
            subjectClassRepository.findByIdAndDeletedFalse(schedule.getSubjectClassId()).ifPresent(cc -> {
                res.setSubjectClassCode(cc.getSubjectClassCode());
                res.setSubjectClassName(cc.getName());
            });
        }
        if (schedule.getTeacherId() != null) {
            teacherRepository.findByIdAndDeletedFalse(schedule.getTeacherId()).ifPresent(t -> {
                res.setTeacherCode(t.getTeacherCode());
                res.setTeacherName(t.getFullName());
            });
        }
        return res;
    }

    private List<ClassScheduleResponse> enrichScheduleResponses(List<ClassSchedule> list) {
        if (list.isEmpty()) return Collections.emptyList();

        Set<Long> scIds = list.stream().map(ClassSchedule::getSubjectClassId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> teacherIds = list.stream().map(ClassSchedule::getTeacherId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, SubjectClass> scMap = subjectClassRepository.findAllByIdInAndDeletedFalse(scIds).stream().collect(Collectors.toMap(SubjectClass::getId, Function.identity()));
        Map<Long, Teacher> teacherMap = teacherRepository.findAllByIdInAndDeletedFalse(teacherIds).stream().collect(Collectors.toMap(Teacher::getId, Function.identity()));

        return list.stream().map(s -> {
            ClassScheduleResponse res = classScheduleMapper.toClassScheduleResponse(s);
            if (s.getSubjectClassId() != null && scMap.containsKey(s.getSubjectClassId())) {
                SubjectClass sc = scMap.get(s.getSubjectClassId());
                res.setSubjectClassCode(sc.getSubjectClassCode());
                res.setSubjectClassName(sc.getName());
            }
            if (s.getTeacherId() != null && teacherMap.containsKey(s.getTeacherId())) {
                Teacher t = teacherMap.get(s.getTeacherId());
                res.setTeacherCode(t.getTeacherCode());
                res.setTeacherName(t.getFullName());
            }
            return res;
        }).toList();
    }
}
