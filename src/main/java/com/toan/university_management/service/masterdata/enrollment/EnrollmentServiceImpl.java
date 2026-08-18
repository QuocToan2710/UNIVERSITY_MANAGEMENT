package com.toan.university_management.service.masterdata.enrollment;

import com.toan.university_management.dto.request.masterdata.EnrollmentRequest;
import com.toan.university_management.dto.response.masterdata.EnrollmentResponse;
import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.entity.masterdata.SubjectClass;
import com.toan.university_management.enums.EnrollmentStatus;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.EnrollmentMapper;
import com.toan.university_management.repository.masterdata.EnrollmentRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.SubjectClassRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {
    EnrollmentRepository enrollmentRepository;
    StudentRepository studentRepository;
    SubjectClassRepository subjectClassRepository;
    EnrollmentMapper enrollmentMapper;

    @Override
    public EnrollmentResponse createEnrollment(EnrollmentRequest request) {
        if (!studentRepository.existsByIdAndDeletedFalse(request.getStudentId())) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }
        if (!subjectClassRepository.existsByIdAndDeletedFalse(request.getSubjectClassId())) {
            throw new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND);
        }
        if (enrollmentRepository.existsByStudentIdAndSubjectClassIdAndDeletedFalse(request.getStudentId(), request.getSubjectClassId())) {
            throw new AppException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }

        Enrollment enrollment = enrollmentMapper.toEnrollment(request);
        if (enrollment.getEnrollmentCode() == null || enrollment.getEnrollmentCode().isBlank()) {
            enrollment.setEnrollmentCode("ENR_" + System.currentTimeMillis());
        }
        enrollment.setEnrolledAt(LocalDateTime.now());
        calculateTotalScore(enrollment);

        return enrichResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment e = enrollmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        return enrichResponse(e);
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrichResponses(enrollmentRepository.findAllByDeletedFalse());
    }

    @Override
    public Page<EnrollmentResponse> getAllEnrollments(Pageable pageable) {
        Page<Enrollment> page = enrollmentRepository.findAllByDeletedFalse(pageable);
        List<EnrollmentResponse> content = enrichResponses(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public EnrollmentResponse updateEnrollment(Long id, EnrollmentRequest request) {
        Enrollment e = enrollmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        enrollmentMapper.updateEnrollment(e, request);
        calculateTotalScore(e);
        e = enrollmentRepository.save(e);
        return enrichResponse(e);
    }

    @Override
    public void deleteEnrollment(Long id) {
        if (!enrollmentRepository.existsByIdAndDeletedFalse(id)) {
            throw new AppException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }
        enrollmentRepository.deleteById(id);
    }

    private void calculateTotalScore(Enrollment e) {
        if (e.getMidtermScore() != null && e.getFinalScore() != null) {
            double total = e.getMidtermScore() * 0.3 + e.getFinalScore() * 0.7;
            e.setTotalScore(Math.round(total * 100.0) / 100.0);
            if (e.getTotalScore() >= 4.0) {
                e.setStatus(EnrollmentStatus.PASSED);
            } else {
                e.setStatus(EnrollmentStatus.FAILED);
            }
        }
    }

    private EnrollmentResponse enrichResponse(Enrollment e) {
        EnrollmentResponse res = enrollmentMapper.toEnrollmentResponse(e);
        if (e.getStudentId() != null) {
            studentRepository.findByIdAndDeletedFalse(e.getStudentId()).ifPresent(s -> {
                res.setStudentCode(s.getStudentCode());
                res.setStudentName(s.getFullName());
            });
        }
        if (e.getSubjectClassId() != null) {
            subjectClassRepository.findByIdAndDeletedFalse(e.getSubjectClassId()).ifPresent(sc -> {
                res.setSubjectClassCode(sc.getSubjectClassCode());
                res.setSubjectClassName(sc.getName());
            });
        }
        return res;
    }

    private List<EnrollmentResponse> enrichResponses(List<Enrollment> list) {
        if (list.isEmpty()) return Collections.emptyList();
        Set<Long> studentIds = list.stream().map(Enrollment::getStudentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> scIds = list.stream().map(Enrollment::getSubjectClassId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, Student> studentMap = studentRepository.findAllByIdInAndDeletedFalse(studentIds).stream().collect(Collectors.toMap(Student::getId, Function.identity()));
        Map<Long, SubjectClass> scMap = subjectClassRepository.findAllByIdInAndDeletedFalse(scIds).stream().collect(Collectors.toMap(SubjectClass::getId, Function.identity()));

        return list.stream().map(e -> {
            EnrollmentResponse res = enrollmentMapper.toEnrollmentResponse(e);
            if (e.getStudentId() != null && studentMap.containsKey(e.getStudentId())) {
                Student s = studentMap.get(e.getStudentId());
                res.setStudentCode(s.getStudentCode());
                res.setStudentName(s.getFullName());
            }
            if (e.getSubjectClassId() != null && scMap.containsKey(e.getSubjectClassId())) {
                SubjectClass sc = scMap.get(e.getSubjectClassId());
                res.setSubjectClassCode(sc.getSubjectClassCode());
                res.setSubjectClassName(sc.getName());
            }
            return res;
        }).toList();
    }
}
