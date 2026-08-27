package com.toan.university_management.service.masterdata.attendance;

import com.toan.university_management.dto.request.masterdata.AttendanceRecordItemRequest;
import com.toan.university_management.dto.request.masterdata.AttendanceSessionRequest;
import com.toan.university_management.dto.request.masterdata.AutoGenerateSessionsRequest;
import com.toan.university_management.dto.request.masterdata.SubmitAttendanceRequest;
import com.toan.university_management.dto.response.masterdata.AttendanceRecordResponse;
import com.toan.university_management.dto.response.masterdata.AttendanceSessionResponse;
import com.toan.university_management.dto.response.masterdata.BannedStudentResponse;
import com.toan.university_management.dto.response.masterdata.StudentAttendanceSummaryResponse;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.masterdata.*;
import com.toan.university_management.enums.*;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.AttendanceRecordMapper;
import com.toan.university_management.mapper.masterdata.AttendanceSessionMapper;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.masterdata.*;
import com.toan.university_management.service.notification.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    AttendanceSessionRepository attendanceSessionRepository;
    AttendanceRecordRepository attendanceRecordRepository;
    EnrollmentRepository enrollmentRepository;
    SubjectClassRepository subjectClassRepository;
    SubjectRepository subjectRepository;
    ClassScheduleRepository classScheduleRepository;
    StudentRepository studentRepository;
    TeacherRepository teacherRepository;
    ClassGroupRepository classGroupRepository;
    UserRepository userRepository;

    AttendanceSessionMapper attendanceSessionMapper;
    AttendanceRecordMapper attendanceRecordMapper;
    NotificationService notificationService;

    @Override
    public List<AttendanceSessionResponse> autoGenerateSessions(AutoGenerateSessionsRequest request) {
        SubjectClass sc = subjectClassRepository.findByIdAndDeletedFalse(request.getSubjectClassId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        int totalSessions = (request.getTotalSessions() != null && request.getTotalSessions() > 0)
                ? request.getTotalSessions() : 15;

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();

        List<ClassSchedule> schedules = classScheduleRepository.findAllBySubjectClassIdAndDeletedFalse(sc.getId());
        ClassSchedule primarySchedule = schedules.isEmpty() ? null : schedules.get(0);

        List<AttendanceSession> existingSessions = attendanceSessionRepository
                .findAllBySubjectClassIdAndDeletedFalseOrderBySessionNumberAsc(sc.getId());

        int currentCount = existingSessions.size();
        List<AttendanceSession> newSessions = new ArrayList<>();

        LocalDate currentDate = startDate;
        if (primarySchedule != null && primarySchedule.getDayOfWeek() != null) {
            DayOfWeek targetDow = mapWeekDayToDayOfWeek(primarySchedule.getDayOfWeek());
            while (currentDate.getDayOfWeek() != targetDow) {
                currentDate = currentDate.plusDays(1);
            }
        }

        for (int i = 1; i <= totalSessions; i++) {
            int sessionNum = currentCount + i;
            String sessionCode = String.format("ATT-%s-B%02d", sc.getSubjectClassCode(), sessionNum);

            AttendanceSession session = AttendanceSession.builder()
                    .sessionCode(sessionCode)
                    .name("Buổi " + sessionNum + " - " + sc.getName())
                    .subjectClassId(sc.getId())
                    .classScheduleId(primarySchedule != null ? primarySchedule.getId() : null)
                    .teacherId(primarySchedule != null && primarySchedule.getTeacherId() != null ? primarySchedule.getTeacherId() : sc.getTeacherId())
                    .sessionNumber(sessionNum)
                    .sessionDate(currentDate)
                    .lessonCount(3)
                    .room(primarySchedule != null ? primarySchedule.getRoom() : "P.Học")
                    .topic("Nội dung bài học buổi " + sessionNum)
                    .status(AttendanceSessionStatus.PENDING)
                    .build();

            newSessions.add(session);
            currentDate = currentDate.plusWeeks(1);
        }

        List<AttendanceSession> saved = attendanceSessionRepository.saveAll(newSessions);
        return enrichSessionResponses(saved);
    }

    @Override
    public AttendanceSessionResponse createSession(AttendanceSessionRequest request) {
        SubjectClass sc = subjectClassRepository.findByIdAndDeletedFalse(request.getSubjectClassId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        int sessionNum = request.getSessionNumber();
        String sessionCode = String.format("ATT-%s-B%02d", sc.getSubjectClassCode(), sessionNum);

        AttendanceSession session = attendanceSessionMapper.toAttendanceSession(request);
        session.setSessionCode(sessionCode);
        session.setName("Buổi " + sessionNum + " - " + sc.getName());
        session.setStatus(AttendanceSessionStatus.PENDING);

        if (session.getTeacherId() == null) {
            session.setTeacherId(sc.getTeacherId());
        }

        AttendanceSession saved = attendanceSessionRepository.save(session);
        return enrichSessionResponse(saved);
    }

    @Override
    public AttendanceSessionResponse updateSession(Long sessionId, AttendanceSessionRequest request) {
        AttendanceSession session = attendanceSessionRepository.findByIdAndDeletedFalse(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_SESSION_NOT_FOUND));

        attendanceSessionMapper.updateAttendanceSession(session, request);
        AttendanceSession saved = attendanceSessionRepository.save(session);
        return enrichSessionResponse(saved);
    }

    @Override
    public void deleteSession(Long sessionId) {
        AttendanceSession session = attendanceSessionRepository.findByIdAndDeletedFalse(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_SESSION_NOT_FOUND));
        attendanceSessionRepository.delete(session);
    }

    @Override
    public List<AttendanceSessionResponse> getSessionsBySubjectClass(Long subjectClassId) {
        List<AttendanceSession> sessions = attendanceSessionRepository
                .findAllBySubjectClassIdAndDeletedFalseOrderBySessionNumberAsc(subjectClassId);
        return enrichSessionResponses(sessions);
    }

    @Override
    public AttendanceSessionResponse getSessionDetails(Long sessionId) {
        AttendanceSession session = attendanceSessionRepository.findByIdAndDeletedFalse(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_SESSION_NOT_FOUND));
        return enrichSessionResponse(session);
    }

    @Override
    public List<AttendanceRecordResponse> getSessionRecords(Long sessionId) {
        AttendanceSession session = attendanceSessionRepository.findByIdAndDeletedFalse(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_SESSION_NOT_FOUND));

        List<AttendanceRecord> records = attendanceRecordRepository.findAllBySessionIdAndDeletedFalse(sessionId);
        List<Enrollment> enrollments = enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(session.getSubjectClassId());

        Set<Long> studentIds = enrollments.stream().map(Enrollment::getStudentId).collect(Collectors.toSet());
        Map<Long, Student> studentMap = studentRepository.findAllByIdInAndDeletedFalse(studentIds).stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));

        Set<Long> classGroupIds = studentMap.values().stream().map(Student::getClassGroupId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, ClassGroup> groupMap = classGroupRepository.findAllByIdInAndDeletedFalse(classGroupIds).stream()
                .collect(Collectors.toMap(ClassGroup::getId, Function.identity()));

        Map<Long, AttendanceRecord> recordMap = records.stream()
                .collect(Collectors.toMap(AttendanceRecord::getStudentId, Function.identity(), (r1, r2) -> r1));

        List<AttendanceRecordResponse> responses = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            Long studentId = enrollment.getStudentId();
            Student student = studentMap.get(studentId);
            if (student == null) continue;

            AttendanceRecord existingRec = recordMap.get(studentId);
            ClassGroup group = student.getClassGroupId() != null ? groupMap.get(student.getClassGroupId()) : null;

            AttendanceRecordResponse res = AttendanceRecordResponse.builder()
                    .sessionId(sessionId)
                    .sessionNumber(session.getSessionNumber())
                    .sessionDate(session.getSessionDate() != null ? session.getSessionDate().toString() : "")
                    .enrollmentId(enrollment.getId())
                    .studentId(studentId)
                    .studentCode(student.getStudentCode())
                    .studentName(student.getFullName())
                    .classGroupName(group != null ? group.getClassName() : "")
                    .status(existingRec != null ? existingRec.getStatus() : AttendanceStatus.PRESENT)
                    .lateMinutes(existingRec != null ? existingRec.getLateMinutes() : 0)
                    .note(existingRec != null ? existingRec.getNote() : "")
                    .checkedAt(existingRec != null ? existingRec.getCheckedAt() : null)
                    .id(existingRec != null ? existingRec.getId() : null)
                    .build();

            responses.add(res);
        }

        responses.sort(Comparator.comparing(AttendanceRecordResponse::getStudentCode, Comparator.nullsLast(String::compareTo)));
        return responses;
    }

    @Override
    public AttendanceSessionResponse submitAttendance(Long sessionId, SubmitAttendanceRequest request) {
        AttendanceSession session = attendanceSessionRepository.findByIdAndDeletedFalse(sessionId)
                .orElseThrow(() -> new AppException(ErrorCode.ATTENDANCE_SESSION_NOT_FOUND));

        SubjectClass sc = subjectClassRepository.findByIdAndDeletedFalse(session.getSubjectClassId())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        if (request.getTopic() != null && !request.getTopic().isBlank()) {
            session.setTopic(request.getTopic());
        }
        if (request.getNote() != null) {
            session.setNote(request.getNote());
        }
        session.setStatus(AttendanceSessionStatus.COMPLETED);
        attendanceSessionRepository.save(session);

        List<AttendanceRecord> existingRecords = attendanceRecordRepository.findAllBySessionIdAndDeletedFalse(sessionId);
        Map<Long, AttendanceRecord> existingMap = existingRecords.stream()
                .collect(Collectors.toMap(AttendanceRecord::getStudentId, Function.identity(), (a, b) -> a));

        List<AttendanceRecord> toSave = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        List<Enrollment> enrollments = enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(session.getSubjectClassId());
        Map<Long, Enrollment> enrollmentMap = enrollments.stream()
                .collect(Collectors.toMap(Enrollment::getStudentId, Function.identity(), (a, b) -> a));

        for (AttendanceRecordItemRequest item : request.getRecords()) {
            Enrollment enrollment = enrollmentMap.get(item.getStudentId());
            if (enrollment == null) continue;

            AttendanceRecord rec = existingMap.get(item.getStudentId());
            if (rec == null) {
                rec = AttendanceRecord.builder()
                        .sessionId(sessionId)
                        .enrollmentId(enrollment.getId())
                        .studentId(item.getStudentId())
                        .status(item.getStatus() != null ? item.getStatus() : AttendanceStatus.PRESENT)
                        .lateMinutes(item.getLateMinutes() != null ? item.getLateMinutes() : 0)
                        .note(item.getNote())
                        .checkedAt(now)
                        .build();
            } else {
                rec.setStatus(item.getStatus() != null ? item.getStatus() : AttendanceStatus.PRESENT);
                rec.setLateMinutes(item.getLateMinutes() != null ? item.getLateMinutes() : 0);
                rec.setNote(item.getNote());
                rec.setCheckedAt(now);
            }
            toSave.add(rec);
        }

        attendanceRecordRepository.saveAll(toSave);

        // Auto calculate and trigger warnings for all students in this class
        recalculateClassAttendanceAndTriggerWarnings(sc);

        return enrichSessionResponse(session);
    }

    private void recalculateClassAttendanceAndTriggerWarnings(SubjectClass sc) {
        List<AttendanceSession> allSessions = attendanceSessionRepository
                .findAllBySubjectClassIdAndDeletedFalseOrderBySessionNumberAsc(sc.getId());

        int totalPlannedSessions = allSessions.isEmpty() ? 15 : allSessions.size();
        List<AttendanceSession> completedSessions = allSessions.stream()
                .filter(s -> s.getStatus() == AttendanceSessionStatus.COMPLETED)
                .toList();

        int totalCompletedCount = completedSessions.size();
        if (totalCompletedCount == 0) return;

        List<Long> completedSessionIds = completedSessions.stream().map(AttendanceSession::getId).toList();
        List<AttendanceRecord> records = attendanceRecordRepository.findAllBySessionIdInAndDeletedFalse(completedSessionIds);

        Map<Long, List<AttendanceRecord>> studentRecordsMap = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getStudentId));

        List<Enrollment> enrollments = enrollmentRepository.findAllBySubjectClassIdAndDeletedFalse(sc.getId());
        Set<Long> studentIds = enrollments.stream().map(Enrollment::getStudentId).collect(Collectors.toSet());
        Map<Long, Student> studentMap = studentRepository.findAllByIdInAndDeletedFalse(studentIds).stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));

        for (Enrollment enrollment : enrollments) {
            Long studentId = enrollment.getStudentId();
            Student student = studentMap.get(studentId);
            if (student == null) continue;

            List<AttendanceRecord> studentRecs = studentRecordsMap.getOrDefault(studentId, Collections.emptyList());

            int unexcused = 0;
            int excused = 0;
            int late = 0;
            int present = 0;

            for (AttendanceRecord r : studentRecs) {
                if (r.getStatus() == AttendanceStatus.UNEXCUSED) unexcused++;
                else if (r.getStatus() == AttendanceStatus.EXCUSED) excused++;
                else if (r.getStatus() == AttendanceStatus.LATE) late++;
                else if (r.getStatus() == AttendanceStatus.PRESENT) present++;
            }

            // Formula: Attendance Score (out of 10)
            double calculatedScore = 10.0 - (unexcused * 2.0 + excused * 1.0 + late * 0.5);
            calculatedScore = Math.max(0.0, Math.round(calculatedScore * 10.0) / 10.0);

            // Formula: Absence Rate (%) based on total planned sessions
            double effectiveAbsent = unexcused + (excused * 0.5) + (late * 0.25);
            double absenceRate = Math.round((effectiveAbsent / (double) totalPlannedSessions) * 1000.0) / 10.0;

            boolean isBanned = absenceRate > 20.0;

            enrollment.setTotalSessions(totalCompletedCount);
            enrollment.setAbsentSessions((int) Math.round(effectiveAbsent));
            enrollment.setAbsenceRate(absenceRate);
            enrollment.setIsBannedFromExam(isBanned);

            if (isBanned) {
                enrollment.setAttendanceScore(0.0);
            } else {
                enrollment.setAttendanceScore(calculatedScore);
            }

            enrollmentRepository.save(enrollment);

            // Trigger Realtime Warning Notification
            try {
                if (isBanned) {
                    notificationService.sendSystemNotification(
                            "🚫 CẤM THI HỌC PHẦN: " + sc.getName(),
                            "Bạn đã vắng " + absenceRate + "% số tiết môn " + sc.getName() + " (vượt ngưỡng 20%). Bạn đã bị CẤM THI kết thúc học phần!",
                            NotificationType.ATTENDANCE,
                            NotificationPriority.URGENT,
                            NotificationTargetType.USER,
                            student.getStudentCode(),
                            "/student/attendance"
                    );
                } else if (absenceRate >= 10.0) {
                    notificationService.sendSystemNotification(
                            "⚠️ CẢNH BÁO CHUYÊN CẦN: " + sc.getName(),
                            "Cảnh báo: Bạn đã vắng " + absenceRate + "% số tiết môn " + sc.getName() + ". Vui lòng tham gia đầy đủ để tránh bị cấm thi (ngưỡng > 20%)!",
                            NotificationType.ATTENDANCE,
                            NotificationPriority.HIGH,
                            NotificationTargetType.USER,
                            student.getStudentCode(),
                            "/student/attendance"
                    );
                }
            } catch (Exception ex) {
                log.warn("Could not send attendance notification to student {}: {}", student.getStudentCode(), ex.getMessage());
            }
        }
    }

    @Override
    public List<StudentAttendanceSummaryResponse> getMyAttendanceSummary(String semester, String academicYear) {
        Student currentStudent = resolveCurrentStudent();
        if (currentStudent == null) {
            return Collections.emptyList();
        }

        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentIdAndDeletedFalse(currentStudent.getId());
        if (enrollments.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> scIds = enrollments.stream().map(Enrollment::getSubjectClassId).collect(Collectors.toSet());
        Map<Long, SubjectClass> scMap = subjectClassRepository.findAllByIdInAndDeletedFalse(scIds).stream()
                .collect(Collectors.toMap(SubjectClass::getId, Function.identity()));

        Set<Long> subIds = scMap.values().stream().map(SubjectClass::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Subject> subMap = subjectRepository.findAllByIdInAndDeletedFalse(subIds).stream()
                .collect(Collectors.toMap(Subject::getId, Function.identity()));

        Set<Long> teacherIds = scMap.values().stream().map(SubjectClass::getTeacherId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Teacher> teacherMap = teacherRepository.findAllByIdInAndDeletedFalse(teacherIds).stream()
                .collect(Collectors.toMap(Teacher::getId, Function.identity()));

        List<AttendanceSession> allSessions = attendanceSessionRepository.findAllBySubjectClassIdInAndDeletedFalse(scIds);
        Map<Long, List<AttendanceSession>> sessionMap = allSessions.stream()
                .collect(Collectors.groupingBy(AttendanceSession::getSubjectClassId));

        List<AttendanceRecord> myRecords = attendanceRecordRepository.findAllByStudentIdAndDeletedFalse(currentStudent.getId());
        Map<Long, AttendanceRecord> recordMap = myRecords.stream()
                .collect(Collectors.toMap(AttendanceRecord::getSessionId, Function.identity(), (a, b) -> a));

        List<StudentAttendanceSummaryResponse> summaries = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            SubjectClass sc = scMap.get(enrollment.getSubjectClassId());
            if (sc == null) continue;

            if (semester != null && !semester.isBlank() && !"ALL".equalsIgnoreCase(semester.trim())) {
                if (!semester.trim().equalsIgnoreCase(sc.getSemester())) continue;
            }
            if (academicYear != null && !academicYear.isBlank() && !"ALL".equalsIgnoreCase(academicYear.trim())) {
                if (!academicYear.trim().equalsIgnoreCase(sc.getAcademicYear())) continue;
            }

            Subject sub = sc.getSubjectId() != null ? subMap.get(sc.getSubjectId()) : null;
            Teacher teacher = sc.getTeacherId() != null ? teacherMap.get(sc.getTeacherId()) : null;

            List<AttendanceSession> sessions = sessionMap.getOrDefault(sc.getId(), Collections.emptyList());
            int totalPlanned = sessions.isEmpty() ? 15 : sessions.size();
            List<AttendanceSession> completed = sessions.stream()
                    .filter(s -> s.getStatus() == AttendanceSessionStatus.COMPLETED).toList();

            int attended = 0;
            int excused = 0;
            int unexcused = 0;
            int late = 0;

            List<AttendanceRecordResponse> recordResponses = new ArrayList<>();
            for (AttendanceSession s : sessions) {
                AttendanceRecord r = recordMap.get(s.getId());
                AttendanceStatus st = r != null ? r.getStatus() : (s.getStatus() == AttendanceSessionStatus.COMPLETED ? AttendanceStatus.PRESENT : AttendanceStatus.PRESENT);
                if (s.getStatus() == AttendanceSessionStatus.COMPLETED) {
                    if (st == AttendanceStatus.PRESENT) attended++;
                    else if (st == AttendanceStatus.EXCUSED) excused++;
                    else if (st == AttendanceStatus.UNEXCUSED) unexcused++;
                    else if (st == AttendanceStatus.LATE) late++;
                }

                recordResponses.add(AttendanceRecordResponse.builder()
                        .sessionId(s.getId())
                        .sessionNumber(s.getSessionNumber())
                        .sessionDate(s.getSessionDate() != null ? s.getSessionDate().toString() : "")
                        .studentId(currentStudent.getId())
                        .studentCode(currentStudent.getStudentCode())
                        .studentName(currentStudent.getFullName())
                        .status(r != null ? r.getStatus() : (s.getStatus() == AttendanceSessionStatus.COMPLETED ? AttendanceStatus.PRESENT : null))
                        .lateMinutes(r != null ? r.getLateMinutes() : 0)
                        .note(r != null ? r.getNote() : s.getTopic())
                        .checkedAt(r != null ? r.getCheckedAt() : null)
                        .build());
            }

            double absenceRate = enrollment.getAbsenceRate() != null ? enrollment.getAbsenceRate() : 0.0;
            boolean isBanned = Boolean.TRUE.equals(enrollment.getIsBannedFromExam());

            String examStatus = "ELIGIBLE";
            if (isBanned) {
                examStatus = "BANNED";
            } else if (absenceRate >= 10.0) {
                examStatus = "AT_RISK";
            }

            StudentAttendanceSummaryResponse summary = StudentAttendanceSummaryResponse.builder()
                    .enrollmentId(enrollment.getId())
                    .subjectClassId(sc.getId())
                    .subjectClassCode(sc.getSubjectClassCode())
                    .subjectClassName(sc.getName())
                    .subjectName(sub != null ? sub.getName() : sc.getName())
                    .credits(sub != null ? sub.getCredit() : 3)
                    .teacherName(teacher != null ? teacher.getFullName() : "")
                    .totalPlannedSessions(totalPlanned)
                    .completedSessions(completed.size())
                    .attendedSessions(attended)
                    .excusedAbsentSessions(excused)
                    .unexcusedAbsentSessions(unexcused)
                    .lateSessions(late)
                    .absenceRate(absenceRate)
                    .attendanceScore(enrollment.getAttendanceScore() != null ? enrollment.getAttendanceScore() : 10.0)
                    .isBannedFromExam(isBanned)
                    .examStatus(examStatus)
                    .records(recordResponses)
                    .build();

            summaries.add(summary);
        }

        return summaries;
    }

    @Override
    public StudentAttendanceSummaryResponse getMyAttendanceDetails(Long subjectClassId) {
        Student currentStudent = resolveCurrentStudent();
        if (currentStudent == null) {
            throw new AppException(ErrorCode.STUDENT_NOT_FOUND);
        }

        Enrollment enrollment = enrollmentRepository
                .findByStudentIdAndSubjectClassIdAndDeletedFalse(currentStudent.getId(), subjectClassId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        SubjectClass sc = subjectClassRepository.findByIdAndDeletedFalse(subjectClassId)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_CLASS_NOT_FOUND));

        Subject sub = sc.getSubjectId() != null ? subjectRepository.findByIdAndDeletedFalse(sc.getSubjectId()).orElse(null) : null;
        Teacher teacher = sc.getTeacherId() != null ? teacherRepository.findByIdAndDeletedFalse(sc.getTeacherId()).orElse(null) : null;

        List<AttendanceSession> sessions = attendanceSessionRepository
                .findAllBySubjectClassIdAndDeletedFalseOrderBySessionNumberAsc(subjectClassId);

        List<AttendanceRecord> myRecords = attendanceRecordRepository.findAllByStudentIdAndDeletedFalse(currentStudent.getId());
        Map<Long, AttendanceRecord> recordMap = myRecords.stream()
                .collect(Collectors.toMap(AttendanceRecord::getSessionId, Function.identity(), (a, b) -> a));

        List<AttendanceRecordResponse> records = new ArrayList<>();
        int attended = 0, excused = 0, unexcused = 0, late = 0;

        for (AttendanceSession s : sessions) {
            AttendanceRecord r = recordMap.get(s.getId());
            if (s.getStatus() == AttendanceSessionStatus.COMPLETED) {
                AttendanceStatus st = r != null ? r.getStatus() : AttendanceStatus.PRESENT;
                if (st == AttendanceStatus.PRESENT) attended++;
                else if (st == AttendanceStatus.EXCUSED) excused++;
                else if (st == AttendanceStatus.UNEXCUSED) unexcused++;
                else if (st == AttendanceStatus.LATE) late++;
            }

            records.add(AttendanceRecordResponse.builder()
                    .sessionId(s.getId())
                    .sessionNumber(s.getSessionNumber())
                    .sessionDate(s.getSessionDate() != null ? s.getSessionDate().toString() : "")
                    .studentId(currentStudent.getId())
                    .studentCode(currentStudent.getStudentCode())
                    .studentName(currentStudent.getFullName())
                    .status(r != null ? r.getStatus() : (s.getStatus() == AttendanceSessionStatus.COMPLETED ? AttendanceStatus.PRESENT : null))
                    .lateMinutes(r != null ? r.getLateMinutes() : 0)
                    .note(r != null ? r.getNote() : s.getTopic())
                    .checkedAt(r != null ? r.getCheckedAt() : null)
                    .build());
        }

        double absenceRate = enrollment.getAbsenceRate() != null ? enrollment.getAbsenceRate() : 0.0;
        boolean isBanned = Boolean.TRUE.equals(enrollment.getIsBannedFromExam());

        String examStatus = isBanned ? "BANNED" : (absenceRate >= 10.0 ? "AT_RISK" : "ELIGIBLE");

        return StudentAttendanceSummaryResponse.builder()
                .enrollmentId(enrollment.getId())
                .subjectClassId(sc.getId())
                .subjectClassCode(sc.getSubjectClassCode())
                .subjectClassName(sc.getName())
                .subjectName(sub != null ? sub.getName() : sc.getName())
                .credits(sub != null ? sub.getCredit() : 3)
                .teacherName(teacher != null ? teacher.getFullName() : "")
                .totalPlannedSessions(sessions.isEmpty() ? 15 : sessions.size())
                .completedSessions((int) sessions.stream().filter(s -> s.getStatus() == AttendanceSessionStatus.COMPLETED).count())
                .attendedSessions(attended)
                .excusedAbsentSessions(excused)
                .unexcusedAbsentSessions(unexcused)
                .lateSessions(late)
                .absenceRate(absenceRate)
                .attendanceScore(enrollment.getAttendanceScore() != null ? enrollment.getAttendanceScore() : 10.0)
                .isBannedFromExam(isBanned)
                .examStatus(examStatus)
                .records(records)
                .build();
    }

    @Override
    public List<BannedStudentResponse> getBannedStudents(String semester, String academicYear, Long subjectClassId) {
        List<Enrollment> bannedEnrollments;
        if (subjectClassId != null) {
            bannedEnrollments = enrollmentRepository.findAllBySubjectClassIdAndIsBannedFromExamTrueAndDeletedFalse(subjectClassId);
        } else {
            bannedEnrollments = enrollmentRepository.findAllByIsBannedFromExamTrueAndDeletedFalse();
        }

        if (bannedEnrollments.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> studentIds = bannedEnrollments.stream().map(Enrollment::getStudentId).collect(Collectors.toSet());
        Map<Long, Student> studentMap = studentRepository.findAllByIdInAndDeletedFalse(studentIds).stream()
                .collect(Collectors.toMap(Student::getId, Function.identity()));

        Set<Long> scIds = bannedEnrollments.stream().map(Enrollment::getSubjectClassId).collect(Collectors.toSet());
        Map<Long, SubjectClass> scMap = subjectClassRepository.findAllByIdInAndDeletedFalse(scIds).stream()
                .collect(Collectors.toMap(SubjectClass::getId, Function.identity()));

        Set<Long> subIds = scMap.values().stream().map(SubjectClass::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Subject> subMap = subjectRepository.findAllByIdInAndDeletedFalse(subIds).stream()
                .collect(Collectors.toMap(Subject::getId, Function.identity()));

        Set<Long> groupIds = studentMap.values().stream().map(Student::getClassGroupId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, ClassGroup> groupMap = classGroupRepository.findAllByIdInAndDeletedFalse(groupIds).stream()
                .collect(Collectors.toMap(ClassGroup::getId, Function.identity()));

        List<BannedStudentResponse> responses = new ArrayList<>();
        for (Enrollment enrollment : bannedEnrollments) {
            SubjectClass sc = scMap.get(enrollment.getSubjectClassId());
            if (sc == null) continue;

            if (semester != null && !semester.isBlank() && !"ALL".equalsIgnoreCase(semester.trim())) {
                if (!semester.trim().equalsIgnoreCase(sc.getSemester())) continue;
            }
            if (academicYear != null && !academicYear.isBlank() && !"ALL".equalsIgnoreCase(academicYear.trim())) {
                if (!academicYear.trim().equalsIgnoreCase(sc.getAcademicYear())) continue;
            }

            Student student = studentMap.get(enrollment.getStudentId());
            if (student == null) continue;

            Subject sub = sc.getSubjectId() != null ? subMap.get(sc.getSubjectId()) : null;
            ClassGroup group = student.getClassGroupId() != null ? groupMap.get(student.getClassGroupId()) : null;

            double absenceRate = enrollment.getAbsenceRate() != null ? enrollment.getAbsenceRate() : 0.0;

            BannedStudentResponse res = BannedStudentResponse.builder()
                    .enrollmentId(enrollment.getId())
                    .studentId(student.getId())
                    .studentCode(student.getStudentCode())
                    .studentName(student.getFullName())
                    .studentEmail(student.getEmail())
                    .classGroupName(group != null ? group.getClassName() : "")
                    .subjectClassId(sc.getId())
                    .subjectClassCode(sc.getSubjectClassCode())
                    .subjectClassName(sc.getName())
                    .subjectName(sub != null ? sub.getName() : sc.getName())
                    .semester(sc.getSemester())
                    .academicYear(sc.getAcademicYear())
                    .totalSessions(enrollment.getTotalSessions() != null ? enrollment.getTotalSessions() : 15)
                    .absentSessions(enrollment.getAbsentSessions() != null ? enrollment.getAbsentSessions() : 0)
                    .absenceRate(absenceRate)
                    .attendanceScore(enrollment.getAttendanceScore() != null ? enrollment.getAttendanceScore() : 0.0)
                    .reason(String.format("Vắng %s%% số tiết học phần (Vượt quá 20%% theo Quy chế Đào tạo)", absenceRate))
                    .build();

            responses.add(res);
        }

        return responses;
    }

    private DayOfWeek mapWeekDayToDayOfWeek(WeekDay weekDay) {
        return switch (weekDay) {
            case MONDAY -> DayOfWeek.MONDAY;
            case TUESDAY -> DayOfWeek.TUESDAY;
            case WEDNESDAY -> DayOfWeek.WEDNESDAY;
            case THURSDAY -> DayOfWeek.THURSDAY;
            case FRIDAY -> DayOfWeek.FRIDAY;
            case SATURDAY -> DayOfWeek.SATURDAY;
            case SUNDAY -> DayOfWeek.SUNDAY;
        };
    }

    private AttendanceSessionResponse enrichSessionResponse(AttendanceSession session) {
        return enrichSessionResponses(Collections.singletonList(session)).get(0);
    }

    private List<AttendanceSessionResponse> enrichSessionResponses(List<AttendanceSession> sessions) {
        if (sessions.isEmpty()) return Collections.emptyList();

        Set<Long> scIds = sessions.stream().map(AttendanceSession::getSubjectClassId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, SubjectClass> scMap = subjectClassRepository.findAllByIdInAndDeletedFalse(scIds).stream()
                .collect(Collectors.toMap(SubjectClass::getId, Function.identity()));

        Set<Long> subIds = scMap.values().stream().map(SubjectClass::getSubjectId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Subject> subMap = subjectRepository.findAllByIdInAndDeletedFalse(subIds).stream()
                .collect(Collectors.toMap(Subject::getId, Function.identity()));

        Set<Long> teacherIds = sessions.stream().map(AttendanceSession::getTeacherId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, Teacher> teacherMap = teacherRepository.findAllByIdInAndDeletedFalse(teacherIds).stream()
                .collect(Collectors.toMap(Teacher::getId, Function.identity()));

        List<Long> sessionIds = sessions.stream().map(AttendanceSession::getId).toList();
        List<AttendanceRecord> records = attendanceRecordRepository.findAllBySessionIdInAndDeletedFalse(sessionIds);
        Map<Long, List<AttendanceRecord>> recordMap = records.stream()
                .collect(Collectors.groupingBy(AttendanceRecord::getSessionId));

        return sessions.stream().map(s -> {
            AttendanceSessionResponse res = attendanceSessionMapper.toAttendanceSessionResponse(s);
            SubjectClass sc = scMap.get(s.getSubjectClassId());
            if (sc != null) {
                res.setSubjectClassCode(sc.getSubjectClassCode());
                res.setSubjectClassName(sc.getName());
                Subject sub = sc.getSubjectId() != null ? subMap.get(sc.getSubjectId()) : null;
                if (sub != null) res.setSubjectName(sub.getName());
            }

            Teacher t = s.getTeacherId() != null ? teacherMap.get(s.getTeacherId()) : null;
            if (t != null) res.setTeacherName(t.getFullName());

            List<AttendanceRecord> recs = recordMap.getOrDefault(s.getId(), Collections.emptyList());
            res.setTotalStudents(recs.size());
            res.setPresentStudents((int) recs.stream().filter(r -> r.getStatus() == AttendanceStatus.PRESENT).count());
            res.setAbsentStudents((int) recs.stream().filter(r -> r.getStatus() == AttendanceStatus.UNEXCUSED || r.getStatus() == AttendanceStatus.EXCUSED).count());
            res.setLateStudents((int) recs.stream().filter(r -> r.getStatus() == AttendanceStatus.LATE).count());

            return res;
        }).toList();
    }

    private Student resolveCurrentStudent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String principalName = auth.getName();
        if (principalName == null || principalName.isBlank() || "anonymousUser".equalsIgnoreCase(principalName)) {
            return null;
        }

        // 1. Check user by username
        User user = userRepository.findByUsername(principalName).orElse(null);
        if (user != null) {
            Student s = studentRepository.findByEmailAndDeletedFalse(user.getEmail()).orElse(null);
            if (s != null) return s;
        }

        // 2. Check student by student code
        Student sByCode = studentRepository.findByStudentCodeAndDeletedFalse(principalName).orElse(null);
        if (sByCode != null) return sByCode;

        // 3. Check student by email
        return studentRepository.findByEmailAndDeletedFalse(principalName).orElse(null);
    }
}
