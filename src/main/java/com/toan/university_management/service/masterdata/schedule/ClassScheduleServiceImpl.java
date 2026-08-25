package com.toan.university_management.service.masterdata.schedule;

import com.toan.university_management.dto.request.masterdata.ClassScheduleRequest;
import com.toan.university_management.dto.response.masterdata.ClassScheduleResponse;
import com.toan.university_management.entity.masterdata.ClassSchedule;
import com.toan.university_management.entity.masterdata.Enrollment;
import com.toan.university_management.entity.masterdata.SubjectClass;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.ClassScheduleMapper;
import com.toan.university_management.repository.masterdata.ClassScheduleRepository;
import com.toan.university_management.repository.masterdata.EnrollmentRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.SubjectClassRepository;
import com.toan.university_management.repository.masterdata.TeacherRepository;
import com.toan.university_management.repository.identity.UserRepository;
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
public class ClassScheduleServiceImpl implements ClassScheduleService {

    ClassScheduleRepository classScheduleRepository;
    SubjectClassRepository subjectClassRepository;
    TeacherRepository teacherRepository;
    ClassScheduleMapper classScheduleMapper;
    EnrollmentRepository enrollmentRepository;
    StudentRepository studentRepository;
    UserRepository userRepository;
    com.toan.university_management.service.notification.NotificationService notificationService;

    @Override
    public ClassScheduleResponse createSchedule(ClassScheduleRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime()))
            throw new AppException(ErrorCode.SCHEDULE_TIME_INVALID);

        if (!subjectClassRepository.existsByIdAndDeletedFalse(request.getSubjectClassId())) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }

        // Kiểm tra conflict giáo viên
        if (request.getTeacherId() != null) {
            if (classScheduleRepository.existsTeacherConflict(
                    request.getTeacherId(), request.getDayOfWeek(),
                    request.getStartTime(), request.getEndTime(), null)) {
                throw new AppException(ErrorCode.SCHEDULE_TEACHER_CONFLICT);
            }
        }

        // Kiểm tra conflict phòng học
        if (request.getRoom() != null && !request.getRoom().isBlank()) {
            if (classScheduleRepository.existsRoomConflict(
                    request.getRoom(), request.getDayOfWeek(),
                    request.getStartTime(), request.getEndTime(), null)) {
                throw new AppException(ErrorCode.SCHEDULE_ROOM_CONFLICT);
            }
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

        if (!subjectClassRepository.existsByIdAndDeletedFalse(request.getSubjectClassId())) {
            throw new AppException(ErrorCode.SUBJECT_NOT_FOUND);
        }

        // Kiểm tra conflict giáo viên (loại trừ schedule hiện tại)
        if (request.getTeacherId() != null) {
            if (classScheduleRepository.existsTeacherConflict(
                    request.getTeacherId(), request.getDayOfWeek(),
                    request.getStartTime(), request.getEndTime(), id)) {
                throw new AppException(ErrorCode.SCHEDULE_TEACHER_CONFLICT);
            }
        }

        // Kiểm tra conflict phòng học (loại trừ schedule hiện tại)
        if (request.getRoom() != null && !request.getRoom().isBlank()) {
            if (classScheduleRepository.existsRoomConflict(
                    request.getRoom(), request.getDayOfWeek(),
                    request.getStartTime(), request.getEndTime(), id)) {
                throw new AppException(ErrorCode.SCHEDULE_ROOM_CONFLICT);
            }
        }

        classScheduleMapper.updateSchedule(schedule, request);
        schedule = classScheduleRepository.save(schedule);

        try {
            notificationService.sendSystemNotification(
                    "Cập nhật thời khóa biểu",
                    "Thời khóa biểu lớp mã " + schedule.getScheduleCode() + " đã được cập nhật sang thứ " + schedule.getDayOfWeek() + ", phòng: " + (schedule.getRoom() != null ? schedule.getRoom() : "Chưa xếp phòng"),
                    com.toan.university_management.enums.NotificationType.SCHEDULE,
                    com.toan.university_management.enums.NotificationPriority.HIGH,
                    com.toan.university_management.enums.NotificationTargetType.SUBJECT_CLASS,
                    String.valueOf(schedule.getSubjectClassId()),
                    "/schedule/class"
            );
        } catch (Exception ex) {
            log.warn("Could not send schedule update notification: {}", ex.getMessage());
        }

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
        return enrichScheduleResponses(classScheduleRepository.findAllByDeletedFalse());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getMySchedule(String semester, String academicYear) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .or(() -> userRepository.findByUsernameIgnoreCase(username))
                .or(() -> userRepository.findByEmail(username))
                .flatMap(u -> {
                    var studentOpt = studentRepository.findByUserIdAndDeletedFalse(u.getId())
                            .or(() -> studentRepository.findByStudentCodeAndDeletedFalse(u.getUsername()))
                            .or(() -> (u.getUserCode() != null && !u.getUserCode().isBlank()) ? studentRepository.findByStudentCodeAndDeletedFalse(u.getUserCode()) : Optional.empty())
                            .or(() -> (u.getEmail() != null && !u.getEmail().isBlank()) ? studentRepository.findByEmailAndDeletedFalse(u.getEmail()) : Optional.empty());
                    if (studentOpt.isPresent()) {
                        return studentOpt.map(student -> getByStudent(student.getId(), semester, academicYear));
                    }
                    var teacherOpt = teacherRepository.findByUserIdAndDeletedFalse(u.getId())
                            .or(() -> teacherRepository.findByTeacherCodeAndDeletedFalse(u.getUsername()))
                            .or(() -> (u.getUserCode() != null && !u.getUserCode().isBlank()) ? teacherRepository.findByTeacherCodeAndDeletedFalse(u.getUserCode()) : Optional.empty())
                            .or(() -> (u.getEmail() != null && !u.getEmail().isBlank()) ? teacherRepository.findByEmailAndDeletedFalse(u.getEmail()) : Optional.empty());
                    return teacherOpt.map(teacher -> getByTeacher(teacher.getId(), semester, academicYear));
                })
                .orElse(Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByTeacher(Long teacherId, String semester, String academicYear) {
        if (teacherId == null) return Collections.emptyList();
        List<ClassSchedule> schedules;
        if ((semester == null || semester.isBlank()) && (academicYear == null || academicYear.isBlank())) {
            schedules = classScheduleRepository.findAllByDeletedFalse()
                    .stream().filter(s -> teacherId.equals(s.getTeacherId())).collect(Collectors.toList());
        } else {
            schedules = classScheduleRepository
                    .findAllByTeacherIdAndSemesterAndAcademicYearAndDeletedFalse(teacherId, semester, academicYear);
        }
        return enrichScheduleResponses(schedules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByClassGroup(Long classGroupId, String semester, String academicYear) {
        if (classGroupId == null) return Collections.emptyList();
        // Lấy tất cả sinh viên trong class group -> lấy enrollments -> lấy subjectClassIds -> lấy schedules
        List<Long> studentIds = studentRepository.findAllByDeletedFalse().stream()
                .filter(s -> classGroupId.equals(s.getClassGroupId()))
                .map(com.toan.university_management.entity.masterdata.Student::getId)
                .collect(Collectors.toList());
        if (studentIds.isEmpty()) return Collections.emptyList();

        Set<Long> subjectClassIds = studentIds.stream()
                .flatMap(sid -> enrollmentRepository.findAllByStudentIdAndDeletedFalse(sid).stream())
                .map(Enrollment::getSubjectClassId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (subjectClassIds.isEmpty()) return Collections.emptyList();

        List<ClassSchedule> schedules = classScheduleRepository
                .findBySubjectClassIdsAndFilters(subjectClassIds, semester, academicYear);
        return enrichScheduleResponses(schedules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getBySubject(Long subjectId, String semester, String academicYear) {
        if (subjectId == null) return Collections.emptyList();
        // Lấy subjectClassIds theo subjectId -> lấy schedules
        Set<Long> subjectClassIds = subjectClassRepository.findAllByDeletedFalse().stream()
                .filter(sc -> subjectId.equals(sc.getSubjectId()))
                .map(com.toan.university_management.entity.masterdata.SubjectClass::getId)
                .collect(Collectors.toSet());
        if (subjectClassIds.isEmpty()) return Collections.emptyList();

        List<ClassSchedule> schedules = classScheduleRepository
                .findBySubjectClassIdsAndFilters(subjectClassIds, semester, academicYear);
        return enrichScheduleResponses(schedules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByStudent(Long studentId, String semester, String academicYear) {
        if (studentId == null) return Collections.emptyList();
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentIdAndDeletedFalse(studentId);
        Set<Long> subjectClassIds = enrollments.stream()
                .map(Enrollment::getSubjectClassId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (subjectClassIds.isEmpty()) return Collections.emptyList();

        List<ClassSchedule> schedules = classScheduleRepository
                .findBySubjectClassIdsAndFilters(subjectClassIds, semester, academicYear);
        return enrichScheduleResponses(schedules);
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
