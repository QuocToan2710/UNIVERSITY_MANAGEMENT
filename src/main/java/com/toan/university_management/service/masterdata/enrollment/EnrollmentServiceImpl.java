package com.toan.university_management.service.masterdata.enrollment;

import com.toan.university_management.dto.request.masterdata.BatchEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.ClassGroupEnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.EnrollmentRequest;
import com.toan.university_management.dto.request.masterdata.StudentRegistrationRequest;
import com.toan.university_management.dto.response.masterdata.AvailableSubjectClassResponse;
import com.toan.university_management.dto.response.masterdata.BatchEnrollmentResultResponse;
import com.toan.university_management.dto.response.masterdata.EnrollmentResponse;
import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.entity.masterdata.SubjectClass;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.enums.NotificationPriority;
import com.toan.university_management.enums.NotificationTargetType;
import com.toan.university_management.enums.NotificationType;
import com.toan.university_management.enums.WeekDay;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.EnrollmentMapper;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.masterdata.ClassScheduleRepository;
import com.toan.university_management.repository.masterdata.EnrollmentRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    SubjectRepository subjectRepository;
    ClassScheduleRepository classScheduleRepository;
    TeacherRepository teacherRepository;
    UserRepository userRepository;
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
        if (enrollment.getEnrolledAt() == null) {
            enrollment.setEnrolledAt(LocalDateTime.now());
        }
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

    @Override
    public EnrollmentResponse registerStudent(StudentRegistrationRequest request) {
        Student targetStudent = null;
        if (request.getStudentId() != null) {
            targetStudent = studentRepository.findByIdAndDeletedFalse(request.getStudentId())
                    .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        } else {
            targetStudent = findCurrentStudentOptional().orElse(null);
            if (targetStudent == null && isCurrentUserAdmin()) {
                targetStudent = studentRepository.findAllByDeletedFalse().stream().findFirst().orElse(null);
            }
        }

        if (targetStudent == null) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        EnrollmentRequest req = EnrollmentRequest.builder()
                .studentId(targetStudent.getId())
                .subjectClassId(request.getSubjectClassId())
                .build();
        return createEnrollment(req);
    }

    @Override
    public void cancelRegistration(Long subjectClassId) {
        Student currentStudent = findCurrentStudentOptional().orElse(null);
        if (currentStudent == null && isCurrentUserAdmin()) {
            currentStudent = studentRepository.findAllByDeletedFalse().stream().findFirst().orElse(null);
        }
        if (currentStudent == null) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        final Student studentForNotif = currentStudent;
        Enrollment e = enrollmentRepository.findByStudentIdAndSubjectClassIdAndDeletedFalse(currentStudent.getId(), subjectClassId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));
        e.setDeleted(true);
        enrollmentRepository.save(e);

        try {
            if (studentForNotif.getUserId() != null) {
                subjectClassRepository.findByIdAndDeletedFalse(subjectClassId).ifPresent(sc -> {
                    notificationService.sendSystemNotification(
                            "Hủy đăng ký học phần",
                            "Bạn đã hủy đăng ký lớp học phần: " + sc.getName() + " (" + sc.getSubjectClassCode() + ")",
                            NotificationType.ENROLLMENT,
                            NotificationPriority.NORMAL,
                            NotificationTargetType.USER,
                            String.valueOf(studentForNotif.getUserId()),
                            "/course-registration"
                    );
                });
            }
        } catch (Exception ex) {
            log.warn("Could not send cancellation notification: {}", ex.getMessage());
        }
    }

    @Override
    public void cancelRegistrationById(Long enrollmentId) {
        Enrollment e = enrollmentRepository.findByIdAndDeletedFalse(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        if (!isCurrentUserAdmin()) {
            Student currentStudent = findCurrentStudentOptional().orElse(null);
            if (currentStudent != null && !currentStudent.getId().equals(e.getStudentId())) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }
        }

        e.setDeleted(true);
        enrollmentRepository.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyRegistrations(String semester, String academicYear) {
        Student currentStudent = findCurrentStudentOptional().orElse(null);
        if (currentStudent == null && isCurrentUserAdmin()) {
            currentStudent = studentRepository.findAllByDeletedFalse().stream().findFirst().orElse(null);
        }
        if (currentStudent == null) {
            return Collections.emptyList();
        }
        List<Enrollment> list = enrollmentRepository.findAllByStudentIdAndDeletedFalse(currentStudent.getId());
        List<EnrollmentResponse> enriched = enrichResponses(list);

        return enriched.stream()
                .filter(res -> matchSemester(res.getSemester(), semester))
                .filter(res -> matchAcademicYear(res.getAcademicYear(), academicYear))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsBySubjectClass(Long subjectClassId) {
        List<Enrollment> list = enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(subjectClassId);
        return enrichResponses(list);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableSubjectClassResponse> getAvailableClassesForRegistration(String semester, String academicYear) {
        List<SubjectClass> subjectClasses = subjectClassRepository.findAllByDeletedFalse();

        if (subjectClasses.isEmpty()) {
            return Collections.emptyList();
        }

        if (semester != null && !semester.isBlank() && !"ALL".equalsIgnoreCase(semester.trim())) {
            subjectClasses = subjectClasses.stream()
                    .filter(sc -> matchSemester(sc.getSemester(), semester))
                    .toList();
        }
        if (academicYear != null && !academicYear.isBlank() && !"ALL".equalsIgnoreCase(academicYear.trim())) {
            subjectClasses = subjectClasses.stream()
                    .filter(sc -> matchAcademicYear(sc.getAcademicYear(), academicYear))
                    .toList();
        }

        if (subjectClasses.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> scIds = subjectClasses.stream().map(SubjectClass::getId).collect(Collectors.toSet());
        Set<Long> subjectIds = subjectClasses.stream().map(SubjectClass::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> teacherIds = subjectClasses.stream().map(SubjectClass::getTeacherId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, com.toan.university_management.entity.masterdata.Subject> subjectMap = subjectRepository.findAllByIdInAndDeletedFalse(subjectIds)
                .stream().collect(Collectors.toMap(com.toan.university_management.entity.masterdata.Subject::getId, Function.identity()));
        Map<Long, Teacher> teacherMap = teacherRepository.findAllByIdInAndDeletedFalse(teacherIds)
                .stream().collect(Collectors.toMap(Teacher::getId, Function.identity()));

        List<com.toan.university_management.entity.masterdata.ClassSchedule> allSchedules = classScheduleRepository.findAllBySubjectClassIdInAndDeletedFalse(scIds);
        Map<Long, List<com.toan.university_management.entity.masterdata.ClassSchedule>> scheduleMap = allSchedules.stream()
                .collect(Collectors.groupingBy(com.toan.university_management.entity.masterdata.ClassSchedule::getSubjectClassId));

        Student currentStudent = findCurrentStudentOptional().orElse(null);
        if (currentStudent == null && isCurrentUserAdmin()) {
            currentStudent = studentRepository.findAllByDeletedFalse().stream().findFirst().orElse(null);
        }
        Map<Long, Long> studentEnrolledMap = new HashMap<>();
        if (currentStudent != null) {
            List<Enrollment> studentEnrollments = enrollmentRepository.findAllByStudentIdAndDeletedFalse(currentStudent.getId());
            for (Enrollment enr : studentEnrollments) {
                if (enr.getSubjectClassId() != null) {
                    studentEnrolledMap.put(enr.getSubjectClassId(), enr.getId());
                }
            }
        }

        List<Enrollment> allEnrollments = enrollmentRepository.findAllBySubjectClassIdInAndDeletedFalse(scIds);
        Map<Long, Long> capacityMap = allEnrollments.stream()
                .collect(Collectors.groupingBy(Enrollment::getSubjectClassId, Collectors.counting()));

        return subjectClasses.stream().map(sc -> {
            com.toan.university_management.entity.masterdata.Subject sub = subjectMap.get(sc.getSubjectId());
            Teacher teacher = teacherMap.get(sc.getTeacherId());
            List<com.toan.university_management.entity.masterdata.ClassSchedule> schList = scheduleMap.getOrDefault(sc.getId(), Collections.emptyList());

            List<AvailableSubjectClassResponse.ScheduleInfo> schDtos = schList.stream().map(s -> {
                int dayVal = 2;
                if (s.getDayOfWeek() != null) {
                    dayVal = switch (s.getDayOfWeek()) {
                        case MONDAY -> 2;
                        case TUESDAY -> 3;
                        case WEDNESDAY -> 4;
                        case THURSDAY -> 5;
                        case FRIDAY -> 6;
                        case SATURDAY -> 7;
                        case SUNDAY -> 8;
                    };
                }
                return AvailableSubjectClassResponse.ScheduleInfo.builder()
                        .id(s.getId())
                        .dayOfWeek(dayVal)
                        .shift(null)
                        .startTime(s.getStartTime() != null ? s.getStartTime().toString() : "")
                        .endTime(s.getEndTime() != null ? s.getEndTime().toString() : "")
                        .room(s.getRoom())
                        .build();
            }).toList();

            boolean isEnrolled = studentEnrolledMap.containsKey(sc.getId());
            Long enrollmentId = studentEnrolledMap.get(sc.getId());
            long currentCount = capacityMap.getOrDefault(sc.getId(), 0L);

            return AvailableSubjectClassResponse.builder()
                    .id(sc.getId())
                    .subjectClassCode(sc.getSubjectClassCode())
                    .name(sc.getName())
                    .subjectId(sc.getSubjectId())
                    .subjectCode(sub != null ? sub.getSubjectCode() : "")
                    .subjectName(sub != null ? sub.getName() : "")
                    .credit(sub != null ? sub.getCredit() : 0)
                    .attendanceCoeff(sub != null ? sub.getAttendanceCoeff() : 1)
                    .midtermCoeff(sub != null ? sub.getMidtermCoeff() : 3)
                    .finalCoeff(sub != null ? sub.getFinalCoeff() : 6)
                    .teacherId(sc.getTeacherId())
                    .teacherCode(teacher != null ? teacher.getTeacherCode() : "")
                    .teacherName(teacher != null ? teacher.getFullName() : "")
                    .semester(sc.getSemester())
                    .academicYear(sc.getAcademicYear())
                    .maxCapacity(sc.getMaxCapacity())
                    .currentCapacity(currentCount)
                    .isEnrolled(isEnrolled)
                    .enrollmentId(enrollmentId)
                    .schedules(schDtos)
                    .build();
        }).toList();
    }

    @Override
    public BatchEnrollmentResultResponse batchEnroll(BatchEnrollmentRequest request) {
        SubjectClass subjectClass = subjectClassRepository.findByIdAndDeletedFalse(request.getSubjectClassId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        List<String> successCodes = new ArrayList<>();
        List<String> failedReasons = new ArrayList<>();

        for (Long studentId : request.getStudentIds()) {
            Student student = studentRepository.findByIdAndDeletedFalse(studentId).orElse(null);
            if (student == null) {
                failedReasons.add("Sinh viên ID " + studentId + " không tồn tại");
                continue;
            }

            try {
                EnrollmentRequest req = EnrollmentRequest.builder()
                        .studentId(studentId)
                        .subjectClassId(subjectClass.getId())
                        .build();
                createEnrollment(req);
                successCodes.add(student.getStudentCode() + " - " + student.getFullName());
            } catch (AppException ae) {
                failedReasons.add(student.getStudentCode() + " (" + student.getFullName() + "): " + ae.getErrorCode().getMessage());
            } catch (Exception ex) {
                failedReasons.add(student.getStudentCode() + " (" + student.getFullName() + "): Lỗi xử lý hệ thống");
            }
        }

        return BatchEnrollmentResultResponse.builder()
                .totalRequested(request.getStudentIds().size())
                .successCount(successCodes.size())
                .failedCount(failedReasons.size())
                .successStudentCodes(successCodes)
                .failedReasons(failedReasons)
                .build();
    }

    @Override
    public BatchEnrollmentResultResponse enrollClassGroup(ClassGroupEnrollmentRequest request) {
        List<Student> students = studentRepository.findAllByClassGroupIdAndDeletedFalse(request.getClassGroupId());
        if (students.isEmpty()) {
            return BatchEnrollmentResultResponse.builder()
                    .totalRequested(0)
                    .successCount(0)
                    .failedCount(0)
                    .successStudentCodes(Collections.emptyList())
                    .failedReasons(List.of("Lớp sinh hoạt không có sinh viên nào"))
                    .build();
        }

        List<Long> studentIds = students.stream().map(Student::getId).toList();
        return batchEnroll(BatchEnrollmentRequest.builder()
                .subjectClassId(request.getSubjectClassId())
                .studentIds(studentIds)
                .build());
    }

    private Student getCurrentStudent() {
        return findCurrentStudentOptional()
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
    }

    private Optional<Student> findCurrentStudentOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }

        String username = auth.getName();
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByUsernameIgnoreCase(username))
                .flatMap(u -> studentRepository.findByUserIdAndDeletedFalse(u.getId()))
                .or(() -> studentRepository.findByStudentCodeAndDeletedFalse(username))
                .or(() -> studentRepository.findByEmailAndDeletedFalse(username));
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

    private boolean matchSemester(String actual, String expected) {
        if (expected == null || expected.isBlank() || "ALL".equalsIgnoreCase(expected.trim())) {
            return true;
        }
        if (actual == null || actual.isBlank()) {
            return true;
        }
        String a = actual.trim().toLowerCase().replaceAll("[^0-9a-z]", "");
        String e = expected.trim().toLowerCase().replaceAll("[^0-9a-z]", "");
        if (a.equals(e)) return true;
        if (a.endsWith(e) || e.endsWith(a)) return true;
        return actual.trim().equalsIgnoreCase(expected.trim());
    }

    private boolean matchAcademicYear(String actual, String expected) {
        if (expected == null || expected.isBlank() || "ALL".equalsIgnoreCase(expected.trim())) {
            return true;
        }
        if (actual == null || actual.isBlank()) {
            return true;
        }
        return actual.trim().equalsIgnoreCase(expected.trim());
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN")
        );
    }
}