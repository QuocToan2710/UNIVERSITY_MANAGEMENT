package com.toan.university_management.service.masterdata.grade;

import com.toan.university_management.dto.request.masterdata.GradeBatchUpdateRequest;
import com.toan.university_management.dto.request.masterdata.GradeItemRequest;
import com.toan.university_management.dto.response.masterdata.EnrollmentResponse;
import com.toan.university_management.dto.response.masterdata.SemesterTranscriptResponse;
import com.toan.university_management.dto.response.masterdata.StudentTranscriptResponse;
import com.toan.university_management.dto.response.masterdata.SubjectClassGradeSummaryResponse;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.masterdata.*;
import com.toan.university_management.enums.*;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.EnrollmentMapper;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.masterdata.*;
import com.toan.university_management.service.notification.NotificationService;
import com.toan.university_management.util.GradeCalculator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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
public class GradeServiceImpl implements GradeService {

    EnrollmentRepository enrollmentRepository;
    SubjectClassRepository subjectClassRepository;
    SubjectRepository subjectRepository;
    StudentRepository studentRepository;
    TeacherRepository teacherRepository;
    ClassGroupRepository classGroupRepository;
    MajorRepository majorRepository;
    UserRepository userRepository;
    EnrollmentMapper enrollmentMapper;
    NotificationService notificationService;

    @Override
    @Transactional(readOnly = true)
    public SubjectClassGradeSummaryResponse getSubjectClassGrades(Long subjectClassId) {
        SubjectClass subjectClass = subjectClassRepository.findByIdAndDeletedFalse(subjectClassId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        Subject subject = null;
        if (subjectClass.getSubjectId() != null) {
            subject = subjectRepository.findByIdAndDeletedFalse(subjectClass.getSubjectId()).orElse(null);
        }

        Teacher teacher = null;
        if (subjectClass.getTeacherId() != null) {
            teacher = teacherRepository.findByIdAndDeletedFalse(subjectClass.getTeacherId()).orElse(null);
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(subjectClassId);
        List<EnrollmentResponse> enrichedEnrollments = enrichEnrollments(enrollments, subjectClass, subject);

        // Grade Statistics
        int totalStudents = enrollments.size();
        int gradedStudents = 0;
        int passedCount = 0;
        int failedCount = 0;
        double sumTotalScore = 0.0;
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("A", 0);
        distribution.put("B+", 0);
        distribution.put("B", 0);
        distribution.put("C+", 0);
        distribution.put("C", 0);
        distribution.put("D+", 0);
        distribution.put("D", 0);
        distribution.put("F", 0);

        GradeStatus overallStatus = GradeStatus.DRAFT;
        if (!enrollments.isEmpty()) {
            boolean allLocked = enrollments.stream().allMatch(e -> e.getGradeStatus() == GradeStatus.LOCKED);
            boolean allPublished = enrollments.stream().allMatch(e -> e.getGradeStatus() == GradeStatus.PUBLISHED || e.getGradeStatus() == GradeStatus.LOCKED);
            boolean anySubmitted = enrollments.stream().anyMatch(e -> e.getGradeStatus() == GradeStatus.SUBMITTED);

            if (allLocked) overallStatus = GradeStatus.LOCKED;
            else if (allPublished) overallStatus = GradeStatus.PUBLISHED;
            else if (anySubmitted) overallStatus = GradeStatus.SUBMITTED;
        }

        for (Enrollment e : enrollments) {
            if (e.getTotalScore() != null) {
                gradedStudents++;
                sumTotalScore += e.getTotalScore();
            }
            if (e.getStatus() == EnrollmentStatus.PASSED) {
                passedCount++;
            } else if (e.getStatus() == EnrollmentStatus.FAILED) {
                failedCount++;
            }
            if (e.getLetterGrade() != null && distribution.containsKey(e.getLetterGrade())) {
                distribution.put(e.getLetterGrade(), distribution.get(e.getLetterGrade()) + 1);
            }
        }

        Double averageScore = gradedStudents > 0 
                ? Math.round((sumTotalScore / gradedStudents) * 100.0) / 100.0 
                : null;

        int attCoeff = (subject != null && subject.getAttendanceCoeff() > 0) ? subject.getAttendanceCoeff() : 1;
        int midCoeff = (subject != null && subject.getMidtermCoeff() > 0) ? subject.getMidtermCoeff() : 3;
        int finCoeff = (subject != null && subject.getFinalCoeff() > 0) ? subject.getFinalCoeff() : 6;

        return SubjectClassGradeSummaryResponse.builder()
                .subjectClassId(subjectClass.getId())
                .subjectClassCode(subjectClass.getSubjectClassCode())
                .subjectClassName(subjectClass.getName())
                .semester(subjectClass.getSemester())
                .academicYear(subjectClass.getAcademicYear())
                .maxCapacity(subjectClass.getMaxCapacity())
                .subjectId(subject != null ? subject.getId() : null)
                .subjectCode(subject != null ? subject.getSubjectCode() : null)
                .subjectName(subject != null ? subject.getName() : null)
                .credit(subject != null ? subject.getCredit() : 0)
                .attendanceCoeff(attCoeff)
                .midtermCoeff(midCoeff)
                .finalCoeff(finCoeff)
                .teacherId(teacher != null ? teacher.getId() : null)
                .teacherCode(teacher != null ? teacher.getTeacherCode() : null)
                .teacherName(teacher != null ? teacher.getFullName() : null)
                .gradeStatus(overallStatus)
                .totalStudents(totalStudents)
                .gradedStudents(gradedStudents)
                .passedCount(passedCount)
                .failedCount(failedCount)
                .averageScore(averageScore)
                .gradeDistribution(distribution)
                .studentGrades(enrichedEnrollments)
                .build();
    }

    @Override
    public SubjectClassGradeSummaryResponse updateBatchGrades(Long subjectClassId, GradeBatchUpdateRequest request) {
        SubjectClass subjectClass = subjectClassRepository.findByIdAndDeletedFalse(subjectClassId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        Subject subject = null;
        if (subjectClass.getSubjectId() != null) {
            subject = subjectRepository.findByIdAndDeletedFalse(subjectClass.getSubjectId()).orElse(null);
        }

        List<Enrollment> existingEnrollments = enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(subjectClassId);
        Map<Long, Enrollment> enrollmentMap = existingEnrollments.stream()
                .collect(Collectors.toMap(Enrollment::getId, Function.identity()));

        for (GradeItemRequest item : request.getItems()) {
            Enrollment enrollment = enrollmentMap.get(item.getEnrollmentId());
            if (enrollment == null) {
                continue;
            }

            if (enrollment.getGradeStatus() == GradeStatus.LOCKED) {
                throw new AppException(ErrorCode.GRADE_LOCKED);
            }

            validateScore(item.getAttendanceScore());
            validateScore(item.getMidtermScore());
            validateScore(item.getFinalScore());

            enrollment.setAttendanceScore(item.getAttendanceScore());
            enrollment.setMidtermScore(item.getMidtermScore());
            enrollment.setFinalScore(item.getFinalScore());
            if (item.getNote() != null) {
                enrollment.setNote(item.getNote());
            }

            GradeCalculator.computeAndApplyGrades(enrollment, subject);
            enrollmentRepository.save(enrollment);
        }

        return getSubjectClassGrades(subjectClassId);
    }

    @Override
    public SubjectClassGradeSummaryResponse submitGrades(Long subjectClassId) {
        SubjectClass subjectClass = subjectClassRepository.findByIdAndDeletedFalse(subjectClassId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        List<Enrollment> enrollments = enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(subjectClassId);
        if (enrollments.isEmpty()) {
            return getSubjectClassGrades(subjectClassId);
        }

        for (Enrollment e : enrollments) {
            if (e.getGradeStatus() != GradeStatus.LOCKED && e.getGradeStatus() != GradeStatus.PUBLISHED) {
                e.setGradeStatus(GradeStatus.SUBMITTED);
                enrollmentRepository.save(e);
            }
        }

        try {
            notificationService.sendSystemNotification(
                    "Bảng điểm mới cần duyệt",
                    "Giảng viên đã nộp bảng điểm lớp: " + subjectClass.getName() + " (" + subjectClass.getSubjectClassCode() + ")",
                    NotificationType.GRADE,
                    NotificationPriority.HIGH,
                    NotificationTargetType.ROLE,
                    "ADMIN",
                    "/courses"
            );
        } catch (Exception ex) {
            log.warn("Could not send submit grade notification: {}", ex.getMessage());
        }

        return getSubjectClassGrades(subjectClassId);
    }

    @Override
    public SubjectClassGradeSummaryResponse publishGrades(Long subjectClassId) {
        SubjectClass subjectClass = subjectClassRepository.findByIdAndDeletedFalse(subjectClassId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        List<Enrollment> enrollments = enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(subjectClassId);
        if (enrollments.isEmpty()) {
            return getSubjectClassGrades(subjectClassId);
        }

        for (Enrollment e : enrollments) {
            if (e.getGradeStatus() != GradeStatus.LOCKED) {
                e.setGradeStatus(GradeStatus.PUBLISHED);
                enrollmentRepository.save(e);
            }
        }

        // Notify enrolled students
        try {
            Set<Long> studentIds = enrollments.stream().map(Enrollment::getStudentId).filter(Objects::nonNull).collect(Collectors.toSet());
            List<Student> students = studentRepository.findAllByIdInAndDeletedFalse(studentIds);
            for (Student s : students) {
                if (s.getUserId() != null) {
                    notificationService.sendSystemNotification(
                            "Công bố điểm học phần",
                            "Điểm môn học lớp " + subjectClass.getName() + " (" + subjectClass.getSubjectClassCode() + ") đã được công bố chính thức.",
                            NotificationType.GRADE,
                            NotificationPriority.HIGH,
                            NotificationTargetType.USER,
                            String.valueOf(s.getUserId()),
                            "/courses"
                    );
                }
            }
        } catch (Exception ex) {
            log.warn("Could not send publish grade notifications: {}", ex.getMessage());
        }

        return getSubjectClassGrades(subjectClassId);
    }

    @Override
    public SubjectClassGradeSummaryResponse lockGrades(Long subjectClassId) {
        SubjectClass subjectClass = subjectClassRepository.findByIdAndDeletedFalse(subjectClassId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        List<Enrollment> enrollments = enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(subjectClassId);
        for (Enrollment e : enrollments) {
            e.setGradeStatus(GradeStatus.LOCKED);
            enrollmentRepository.save(e);
        }

        return getSubjectClassGrades(subjectClassId);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentTranscriptResponse

    getStudentTranscript(Long studentId) {
        Student student = studentRepository.findByIdAndDeletedFalse(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        String majorName = null;
        if (student.getMajorId() != null) {
            majorName = majorRepository.findByIdAndDeletedFalse(student.getMajorId()).map(Major::getName).orElse(null);
        }

        String className = null;
        if (student.getClassGroupId() != null) {
            className = classGroupRepository.findByIdAndDeletedFalse(student.getClassGroupId()).map(ClassGroup::getClassName).orElse(null);
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentIdAndDeletedFalse(studentId);

        Set<Long> scIds = enrollments.stream().map(Enrollment::getSubjectClassId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, SubjectClass> scMap = scIds.isEmpty()
                ? Collections.emptyMap()
                : subjectClassRepository.findAllByIdInAndDeletedFalse(scIds).stream()
                        .collect(Collectors.toMap(SubjectClass::getId, Function.identity()));

        Set<Long> subjectIds = scMap.values().stream().map(SubjectClass::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Subject> subMap = subjectIds.isEmpty()
                ? Collections.emptyMap()
                : subjectRepository.findAllByIdInAndDeletedFalse(subjectIds).stream()
                        .collect(Collectors.toMap(Subject::getId, Function.identity()));

        // Group enrollments by Semester
        Map<String, List<Enrollment>> semesterMap = new LinkedHashMap<>();
        for (Enrollment e : enrollments) {
            SubjectClass sc = scMap.get(e.getSubjectClassId());
            String key = (sc != null && sc.getSemester() != null ? sc.getSemester() : "Học kỳ 1") + " - " + (sc != null && sc.getAcademicYear() != null ? sc.getAcademicYear() : "2025-2026");
            semesterMap.computeIfAbsent(key, k -> new ArrayList<>()).add(e);
        }

        List<SemesterTranscriptResponse> semesterResponses = new ArrayList<>();
        double cumulativeWeighted4 = 0.0;
        double cumulativeWeighted10 = 0.0;
        int totalRegisteredCredits = 0;
        int totalEarnedCredits = 0;
        int cumulativeCreditsForGpa = 0;

        for (Map.Entry<String, List<Enrollment>> entry : semesterMap.entrySet()) {
            String semKey = entry.getKey();
            List<Enrollment> semEnrollments = entry.getValue();

            String[] parts = semKey.split(" - ");
            String semesterName = parts[0];
            String academicYear = parts.length > 1 ? parts[1] : "";

            List<EnrollmentResponse> enrichedSemEnrollments = new ArrayList<>();
            double semWeighted4 = 0.0;
            double semWeighted10 = 0.0;
            int semTotalCredits = 0;
            int semEarnedCredits = 0;
            int semCreditsForGpa = 0;

            for (Enrollment e : semEnrollments) {
                EnrollmentResponse resp = enrollmentMapper.toEnrollmentResponse(e);
                resp.setStudentCode(student.getStudentCode());
                resp.setStudentName(student.getFullName());

                SubjectClass sc = scMap.get(e.getSubjectClassId());
                if (sc != null) {
                    resp.setSubjectClassCode(sc.getSubjectClassCode());
                    resp.setSubjectClassName(sc.getName());
                    resp.setSemester(sc.getSemester());
                    resp.setAcademicYear(sc.getAcademicYear());

                    Subject sub = subMap.get(sc.getSubjectId());
                    if (sub != null) {
                        resp.setSubjectCode(sub.getSubjectCode());
                        resp.setSubjectName(sub.getName());
                        resp.setCredit(sub.getCredit());

                        int credit = sub.getCredit();
                        semTotalCredits += credit;
                        totalRegisteredCredits += credit;

                        if (e.getStatus() == EnrollmentStatus.PASSED) {
                            semEarnedCredits += credit;
                            totalEarnedCredits += credit;
                        }

                        if (e.getGradePoint4() != null && e.getTotalScore() != null && (e.getStatus() == EnrollmentStatus.PASSED || e.getStatus() == EnrollmentStatus.FAILED)) {
                            semWeighted4 += e.getGradePoint4() * credit;
                            semWeighted10 += e.getTotalScore() * credit;
                            semCreditsForGpa += credit;

                            cumulativeWeighted4 += e.getGradePoint4() * credit;
                            cumulativeWeighted10 += e.getTotalScore() * credit;
                            cumulativeCreditsForGpa += credit;
                        }
                    }
                }
                enrichedSemEnrollments.add(resp);
            }

            Double semGpa4 = semCreditsForGpa > 0 ? Math.round((semWeighted4 / semCreditsForGpa) * 100.0) / 100.0 : null;
            Double semGpa10 = semCreditsForGpa > 0 ? Math.round((semWeighted10 / semCreditsForGpa) * 100.0) / 100.0 : null;

            semesterResponses.add(SemesterTranscriptResponse.builder()
                    .semester(semesterName)
                    .academicYear(academicYear)
                    .semesterGpa4(semGpa4)
                    .semesterGpa10(semGpa10)
                    .semesterCredits(semTotalCredits)
                    .semesterEarnedCredits(semEarnedCredits)
                    .courses(enrichedSemEnrollments)
                    .build());
        }

        Double cumulativeCpa4 = cumulativeCreditsForGpa > 0 
                ? Math.round((cumulativeWeighted4 / cumulativeCreditsForGpa) * 100.0) / 100.0 
                : null;
        Double cumulativeGpa10 = cumulativeCreditsForGpa > 0 
                ? Math.round((cumulativeWeighted10 / cumulativeCreditsForGpa) * 100.0) / 100.0 
                : null;

        String academicRank = GradeCalculator.getAcademicRank(cumulativeCpa4);

        return StudentTranscriptResponse.builder()
                .studentId(student.getId())
                .studentCode(student.getStudentCode())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .className(className)
                .majorName(majorName)
                .academicStatus(student.getStatus() != null ? student.getStatus().name() : "ACTIVE")
                .cumulativeCpa4(cumulativeCpa4)
                .cumulativeGpa10(cumulativeGpa10)
                .totalRegisteredCredits(totalRegisteredCredits)
                .totalEarnedCredits(totalEarnedCredits)
                .academicRank(academicRank)
                .semesters(semesterResponses)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StudentTranscriptResponse getMyTranscript() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByUsernameIgnoreCase(username))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var studentOpt = studentRepository.findByUserIdAndDeletedFalse(user.getId())
                .or(() -> studentRepository.findByStudentCodeAndDeletedFalse(username))
                .or(() -> (user.getUserCode() != null && !user.getUserCode().isBlank()) ? studentRepository.findByStudentCodeAndDeletedFalse(user.getUserCode()) : Optional.empty())
                .or(() -> (user.getEmail() != null && !user.getEmail().isBlank()) ? studentRepository.findByEmailAndDeletedFalse(user.getEmail()) : Optional.empty());

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getUserId() == null || !student.getUserId().equals(user.getId())) {
                student.setUserId(user.getId());
                studentRepository.save(student);
            }
            return getStudentTranscript(student.getId());
        }

        // If user is ADMIN / TEACHER without a personal student profile, fallback to the first active student
        boolean isAdminOrTeacher = auth.getAuthorities().stream().anyMatch(a -> {
            String role = a.getAuthority();
            return role != null && (role.contains("ADMIN") || role.contains("TEACHER"));
        });
        if (isAdminOrTeacher || "admin".equalsIgnoreCase(username)) {
            var firstStudent = studentRepository.findAllByDeletedFalse().stream().findFirst();
            if (firstStudent.isPresent()) {
                return getStudentTranscript(firstStudent.get().getId());
            }
        }

        throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
    }

    private void validateScore(Double score) {
        if (score != null && (score < 0.0 || score > 10.0)) {
            throw new AppException(ErrorCode.GRADE_INVALID_SCORE);
        }
    }

    private List<EnrollmentResponse> enrichEnrollments(List<Enrollment> enrollments, SubjectClass sc, Subject subject) {
        if (enrollments.isEmpty()) return Collections.emptyList();

        Set<Long> studentIds = enrollments.stream().map(Enrollment::getStudentId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Student> studentMap = studentIds.isEmpty()
                ? Collections.emptyMap()
                : studentRepository.findAllByIdInAndDeletedFalse(studentIds).stream()
                        .collect(Collectors.toMap(Student::getId, Function.identity()));

        return enrollments.stream().map(e -> {
            EnrollmentResponse resp = enrollmentMapper.toEnrollmentResponse(e);
            if (e.getStudentId() != null && studentMap.containsKey(e.getStudentId())) {
                Student s = studentMap.get(e.getStudentId());
                resp.setStudentCode(s.getStudentCode());
                resp.setStudentName(s.getFullName());
            }
            if (sc != null) {
                resp.setSubjectClassCode(sc.getSubjectClassCode());
                resp.setSubjectClassName(sc.getName());
                resp.setSemester(sc.getSemester());
                resp.setAcademicYear(sc.getAcademicYear());
            }
            if (subject != null) {
                resp.setSubjectCode(subject.getSubjectCode());
                resp.setSubjectName(subject.getName());
                resp.setCredit(subject.getCredit());
            }
            if (resp.getGradeStatus() == null) {
                resp.setGradeStatus(GradeStatus.DRAFT);
            }
            return resp;
        }).toList();
    }
}
