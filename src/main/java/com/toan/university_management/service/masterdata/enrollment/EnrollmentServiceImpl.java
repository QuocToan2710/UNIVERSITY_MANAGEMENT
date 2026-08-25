package com.toan.university_management.service.masterdata.enrollment;

import com.toan.university_management.dto.request.masterdata.EnrollmentRequest;
import com.toan.university_management.dto.response.masterdata.EnrollmentResponse;
import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.entity.masterdata.SubjectClass;
import com.toan.university_management.enums.EnrollmentStatus;
import com.toan.university_management.enums.NotificationPriority;
import com.toan.university_management.enums.NotificationTargetType;
import com.toan.university_management.enums.NotificationType;
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
    com.toan.university_management.repository.masterdata.SubjectRepository subjectRepository;
    com.toan.university_management.repository.masterdata.ClassScheduleRepository classScheduleRepository;
    EnrollmentMapper enrollmentMapper;
    com.toan.university_management.service.notification.NotificationService notificationService;

    @Override
    public EnrollmentResponse createEnrollment(EnrollmentRequest request) {
        if (!studentRepository.existsByIdAndDeletedFalse(request.getStudentId())) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }
        SubjectClass subjectClass = subjectClassRepository.findByIdAndDeletedFalse(request.getSubjectClassId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        // 1. Sĩ số tối đa
        if (subjectClass.getMaxCapacity() > 0) {
            long currentCount = enrollmentRepository.countBySubjectClassIdAndDeletedFalse(subjectClass.getId());
            if (currentCount >= subjectClass.getMaxCapacity()) {
                throw new AppException(ErrorCode.ENROLLMENT_CAPACITY_FULL);
            }
        }

        // 2. Trùng lớp học phần
        if (enrollmentRepository.existsByStudentIdAndSubjectClassIdAndDeletedFalse(request.getStudentId(), request.getSubjectClassId())) {
            throw new AppException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }

        // 3. Trùng lịch học (Schedule conflict)
        List<com.toan.university_management.entity.masterdata.ClassSchedule> targetSchedules = 
                classScheduleRepository.findAllBySubjectClassIdInAndDeletedFalse(List.of(subjectClass.getId()));
        if (!targetSchedules.isEmpty()) {
            List<Enrollment> existingEnrollments = enrollmentRepository.findAllByStudentIdAndDeletedFalse(request.getStudentId());
            Set<Long> existingClassIds = existingEnrollments.stream()
                    .map(Enrollment::getSubjectClassId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (!existingClassIds.isEmpty()) {
                List<com.toan.university_management.entity.masterdata.ClassSchedule> existingSchedules =
                        classScheduleRepository.findAllBySubjectClassIdInAndDeletedFalse(existingClassIds);

                for (var targetSch : targetSchedules) {
                    for (var existSch : existingSchedules) {
                        if (targetSch.getDayOfWeek() == existSch.getDayOfWeek()) {
                            boolean hasOverlap = targetSch.getStartTime().isBefore(existSch.getEndTime()) 
                                    && targetSch.getEndTime().isAfter(existSch.getStartTime());
                            if (hasOverlap) {
                                throw new AppException(ErrorCode.ENROLLMENT_SCHEDULE_CONFLICT);
                            }
                        }
                    }
                }
            }
        }

        Enrollment enrollment = enrollmentMapper.toEnrollment(request);
        if (enrollment.getEnrollmentCode() == null || enrollment.getEnrollmentCode().isBlank()) {
            enrollment.setEnrollmentCode("ENR_" + System.currentTimeMillis());
        }
        enrollment.setEnrolledAt(LocalDateTime.now());
        calculateTotalScore(enrollment);

        Enrollment saved = enrollmentRepository.save(enrollment);

        try {
            studentRepository.findByIdAndDeletedFalse(saved.getStudentId()).ifPresent(s -> {
                if (s.getUserId() != null) {
                    notificationService.sendSystemNotification(
                            "Đăng ký học phần thành công",
                            "Bạn đã đăng ký thành công vào lớp: " + subjectClass.getName() + " (" + subjectClass.getSubjectClassCode() + ")",
                            NotificationType.ENROLLMENT,
                            NotificationPriority.NORMAL,
                            NotificationTargetType.USER,
                            String.valueOf(s.getUserId()),
                            "/schedule/timetable"
                    );
                }
            });
        } catch (Exception ex) {
            log.warn("Could not send enrollment notification: {}", ex.getMessage());
        }

        return enrichResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long id) {
        Enrollment e = enrollmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        return enrichResponse(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrichResponses(enrollmentRepository.findAllByDeletedFalse());
    }

    @Override
    @Transactional(readOnly = true)
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
        Enrollment e = enrollmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        e.setDeleted(true);
        enrollmentRepository.save(e);
    }

    private void calculateTotalScore(Enrollment e) {
        if (e.getSubjectClassId() != null) {
            subjectClassRepository.findByIdAndDeletedFalse(e.getSubjectClassId()).ifPresent(sc -> {
                if (sc.getSubjectId() != null) {
                    com.toan.university_management.entity.masterdata.Subject subject = 
                            subjectRepository.findByIdAndDeletedFalse(sc.getSubjectId()).orElse(null);
                    com.toan.university_management.util.GradeCalculator.computeAndApplyGrades(e, subject);
                }
            });
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
                res.setSemester(sc.getSemester());
                res.setAcademicYear(sc.getAcademicYear());
                if (sc.getSubjectId() != null) {
                    subjectRepository.findByIdAndDeletedFalse(sc.getSubjectId()).ifPresent(sub -> {
                        res.setSubjectCode(sub.getSubjectCode());
                        res.setSubjectName(sub.getName());
                        res.setCredit(sub.getCredit());
                    });
                }
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

        Set<Long> subjectIds = scMap.values().stream().map(SubjectClass::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, com.toan.university_management.entity.masterdata.Subject> subMap = subjectRepository.findAllByIdInAndDeletedFalse(subjectIds).stream().collect(Collectors.toMap(com.toan.university_management.entity.masterdata.Subject::getId, Function.identity()));

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
                res.setSemester(sc.getSemester());
                res.setAcademicYear(sc.getAcademicYear());
                if (sc.getSubjectId() != null && subMap.containsKey(sc.getSubjectId())) {
                    var sub = subMap.get(sc.getSubjectId());
                    res.setSubjectCode(sub.getSubjectCode());
                    res.setSubjectName(sub.getName());
                    res.setCredit(sub.getCredit());
                }
            }
            return res;
        }).toList();
    }
}
