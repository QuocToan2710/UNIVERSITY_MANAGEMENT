package com.toan.university_management.service.masterdata.subject;

import com.toan.university_management.dto.request.masterdata.SubjectRequest;
import com.toan.university_management.dto.response.masterdata.SubjectResponse;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.SubjectMapper;
import com.toan.university_management.repository.masterdata.DepartmentRepository;
import com.toan.university_management.repository.masterdata.SubjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class SubjectServiceImpl implements SubjectService {
    SubjectRepository subjectRepository;
    DepartmentRepository departmentRepository;
    SubjectMapper subjectMapper;

    @Override
    public SubjectResponse createSubject(SubjectRequest request) {
        if (subjectRepository.existsBySubjectCodeAndDeletedFalse(request.getSubjectCode())) {
            throw new AppException(ErrorCode.COURSE_EXISTED);
        }
        if (request.getDepartmentId() != null && !departmentRepository.existsByIdAndDeletedFalse(request.getDepartmentId())) {
            throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        Subject subject = subjectMapper.toSubject(request);
        subject = subjectRepository.save(subject);
        return subjectMapper.toSubjectResponse(subject);
    }

    @Override
    public SubjectResponse getSubjectById(Long id) {
        Subject subject = subjectRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        return subjectMapper.toSubjectResponse(subject);
    }

    @Override
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAllByDeletedFalse().stream()
                .map(subjectMapper::toSubjectResponse)
                .toList();
    }

    @Override
    public Page<SubjectResponse> getAllSubjects(Pageable pageable) {
        return subjectRepository.findAllByDeletedFalse(pageable)
                .map(subjectMapper::toSubjectResponse);
    }

    @Override
    public SubjectResponse updateSubject(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_NOT_FOUND));
        if (request.getDepartmentId() != null && !departmentRepository.existsByIdAndDeletedFalse(request.getDepartmentId())) {
            throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        subjectMapper.updateSubject(subject, request);
        subject = subjectRepository.save(subject);
        return subjectMapper.toSubjectResponse(subject);
    }

    @Override
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }
        subjectRepository.deleteById(id);
    }
}
