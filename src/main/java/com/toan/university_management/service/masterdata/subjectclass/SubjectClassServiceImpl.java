package com.toan.university_management.service.masterdata.subjectclass;

import com.toan.university_management.dto.request.masterdata.SubjectClassRequest;
import com.toan.university_management.dto.response.masterdata.SubjectClassResponse;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.entity.masterdata.SubjectClass;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.SubjectClassMapper;
import com.toan.university_management.repository.masterdata.SubjectClassRepository;
import com.toan.university_management.repository.masterdata.SubjectRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class SubjectClassServiceImpl implements SubjectClassService {

    SubjectClassRepository subjectClassRepository;
    SubjectRepository subjectRepository;
    TeacherRepository teacherRepository;
    SubjectClassMapper subjectClassMapper;

    @Override
    public SubjectClassResponse createSubjectClass(SubjectClassRequest request) {
        if (request.getSubjectClassCode() != null && !request.getSubjectClassCode().isBlank()
                && subjectClassRepository.existsBySubjectClassCodeAndDeletedFalse(request.getSubjectClassCode())) {
            throw new AppException(ErrorCode.DATA_INTEGRITY_VIOLATION);
        }

        SubjectClass subjectClass = subjectClassMapper.toSubjectClass(request);
        if (subjectClass.getSubjectClassCode() == null || subjectClass.getSubjectClassCode().isBlank()) {
            subjectClass.setSubjectClassCode("SUBJ_CLASS_" + System.currentTimeMillis());
        }

        subjectClass = subjectClassRepository.save(subjectClass);
        return enrichResponse(subjectClass);
    }

    @Override
    public SubjectClassResponse updateSubjectClass(Long id, SubjectClassRequest request) {
        SubjectClass subjectClass = subjectClassRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));

        subjectClassMapper.updateSubjectClass(subjectClass, request);
        subjectClass = subjectClassRepository.save(subjectClass);
        return enrichResponse(subjectClass);
    }

    @Override
    public void deleteSubjectClass(Long id) {
        if (!subjectClassRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        subjectClassRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectClassResponse getSubjectClassById(Long id) {
        SubjectClass subjectClass = subjectClassRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        return enrichResponse(subjectClass);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SubjectClassResponse> getAllSubjectClasses(Pageable pageable) {
        Page<SubjectClass> page = subjectClassRepository.findAllByDeletedFalse(pageable);
        List<SubjectClassResponse> content = enrichResponses(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectClassResponse> getAllSubjectClasses() {
        return enrichResponses(subjectClassRepository.findAllByDeletedFalse());
    }

    private SubjectClassResponse enrichResponse(SubjectClass item) {
        SubjectClassResponse res = subjectClassMapper.toSubjectClassResponse(item);
        if (item.getSubjectId() != null) {
            subjectRepository.findByIdAndDeletedFalse(item.getSubjectId()).ifPresent(s -> {
                res.setSubjectCode(s.getSubjectCode());
                res.setSubjectName(s.getName());
            });
        }
        if (item.getTeacherId() != null) {
            teacherRepository.findByIdAndDeletedFalse(item.getTeacherId()).ifPresent(t -> {
                res.setTeacherCode(t.getTeacherCode());
                res.setTeacherName(t.getFullName());
            });
        }
        return res;
    }

    private List<SubjectClassResponse> enrichResponses(List<SubjectClass> items) {
        if (items.isEmpty()) return Collections.emptyList();

        Set<Long> subjectIds = items.stream().map(SubjectClass::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> teacherIds = items.stream().map(SubjectClass::getTeacherId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, Subject> subjectMap = subjectRepository.findAllByIdInAndDeletedFalse(subjectIds).stream()
                .collect(Collectors.toMap(Subject::getId, Function.identity()));
        Map<Long, Teacher> teacherMap = teacherRepository.findAllByIdInAndDeletedFalse(teacherIds).stream()
                .collect(Collectors.toMap(Teacher::getId, Function.identity()));

        return items.stream().map(item -> {
            SubjectClassResponse res = subjectClassMapper.toSubjectClassResponse(item);
            if (item.getSubjectId() != null && subjectMap.containsKey(item.getSubjectId())) {
                Subject s = subjectMap.get(item.getSubjectId());
                res.setSubjectCode(s.getSubjectCode());
                res.setSubjectName(s.getName());
            }
            if (item.getTeacherId() != null && teacherMap.containsKey(item.getTeacherId())) {
                Teacher t = teacherMap.get(item.getTeacherId());
                res.setTeacherCode(t.getTeacherCode());
                res.setTeacherName(t.getFullName());
            }
            return res;
        }).toList();
    }
}
