package com.toan.university_management.service.masterdata.schedule;

import com.toan.university_management.dto.request.masterdata.ClassScheduleRequest;
import com.toan.university_management.dto.response.masterdata.ClassScheduleResponse;
import com.toan.university_management.entity.masterdata.ClassGroup;
import com.toan.university_management.entity.masterdata.ClassSchedule;
import com.toan.university_management.entity.masterdata.Course;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.ClassScheduleMapper;
import com.toan.university_management.repository.masterdata.ClassGroupRepository;
import com.toan.university_management.repository.masterdata.ClassScheduleRepository;
import com.toan.university_management.repository.masterdata.CourseRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.TeacherRepository;
import com.toan.university_management.service.masterdata.schedule.ClassScheduleService;
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
public class ClassScheduleServiceImpl implements ClassScheduleService {

    ClassScheduleRepository classScheduleRepository;
    CourseRepository courseRepository;
    TeacherRepository teacherRepository;
    ClassGroupRepository classGroupRepository;
    StudentRepository studentRepository;
    ClassScheduleMapper classScheduleMapper;


    /** Validate time và kiểm tra conflict, excludeId dùng để loại trừ khi update */
    private void validateAndCheckConflicts(ClassScheduleRequest request, String excludeId) {
        if (!request.getEndTime().isAfter(request.getStartTime()))
            throw new AppException(ErrorCode.SCHEDULE_TIME_INVALID);

        if (classScheduleRepository.existsTeacherConflict(
                request.getTeacherId(), request.getDayOfWeek(),
                request.getStartTime(), request.getEndTime(),
                request.getSemester(), request.getAcademicYear(), excludeId))
            throw new AppException(ErrorCode.SCHEDULE_TEACHER_CONFLICT);

        if (request.getRoom() != null && !request.getRoom().isBlank() &&
                classScheduleRepository.existsRoomConflict(
                        request.getRoom(), request.getDayOfWeek(),
                        request.getStartTime(), request.getEndTime(),
                        request.getSemester(), request.getAcademicYear(), excludeId))
            throw new AppException(ErrorCode.SCHEDULE_ROOM_CONFLICT);
    }

    @Override
    public ClassScheduleResponse createSchedule(ClassScheduleRequest request) {
        validateAndCheckConflicts(request, "__NONE__");

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        ClassGroup classGroup = classGroupRepository.findById(request.getClassGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND));

        ClassSchedule schedule = classScheduleMapper.toClassSchedule(request);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setClassGroup(classGroup);

        return classScheduleMapper.toClassScheduleResponse(classScheduleRepository.save(schedule));
    }

    @Override
    public ClassScheduleResponse updateSchedule(String id, ClassScheduleRequest request) {
        ClassSchedule schedule = classScheduleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        validateAndCheckConflicts(request, id);

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        ClassGroup classGroup = classGroupRepository.findById(request.getClassGroupId())
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_GROUP_NOT_FOUND));

        classScheduleMapper.updateClassSchedule(schedule, request);
        schedule.setCourse(course);
        schedule.setTeacher(teacher);
        schedule.setClassGroup(classGroup);

        return classScheduleMapper.toClassScheduleResponse(classScheduleRepository.save(schedule));
    }

    @Override
    public void deleteSchedule(String id) {
        if (!classScheduleRepository.existsById(id))
            throw new AppException(ErrorCode.SCHEDULE_NOT_FOUND);
        classScheduleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ClassScheduleResponse getScheduleById(String id) {
        return classScheduleMapper.toClassScheduleResponse(
                classScheduleRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClassScheduleResponse> getAllSchedules(Pageable pageable) {
        return classScheduleRepository.findAll(pageable)
                .map(classScheduleMapper::toClassScheduleResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByTeacher(String teacherId, String semester, String academicYear) {
        return classScheduleMapper.toClassScheduleResponseList(
                classScheduleRepository.findByTeacherIdAndSemesterAndAcademicYear(teacherId, semester, academicYear));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByClassGroup(String classGroupId, String semester, String academicYear) {
        return classScheduleMapper.toClassScheduleResponseList(
                classScheduleRepository.findByClassGroupIdAndSemesterAndAcademicYear(classGroupId, semester, academicYear));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByCourse(String courseId, String semester, String academicYear) {
        return classScheduleMapper.toClassScheduleResponseList(
                classScheduleRepository.findByCourseIdAndSemesterAndAcademicYear(courseId, semester, academicYear));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getByStudent(String studentId, String semester, String academicYear) {
        return classScheduleMapper.toClassScheduleResponseList(
                classScheduleRepository.findByStudentId(studentId, semester, academicYear));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassScheduleResponse> getMySchedule(String semester, String academicYear) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Kiểm tra nếu user là sinh viên
        var studentOpt = studentRepository.findAll().stream()
                .filter(s -> username.equalsIgnoreCase(s.getStudentCode()) || username.equalsIgnoreCase(s.getEmail()))
                .findFirst();
        if (studentOpt.isPresent() && studentOpt.get().getClassGroup() != null) {
            return getByClassGroup(studentOpt.get().getClassGroup().getId(), semester, academicYear);
        }

        // 2. Kiểm tra nếu user là giảng viên
        var teacherOpt = teacherRepository.findAll().stream()
                .filter(t -> username.equalsIgnoreCase(t.getTeacherCode()) || username.equalsIgnoreCase(t.getEmail()))
                .findFirst();
        if (teacherOpt.isPresent()) {
            return getByTeacher(teacherOpt.get().getId(), semester, academicYear);
        }

        // 3. Admin hoặc khác: trả toàn bộ lịch theo học kỳ + năm học
        return classScheduleRepository.findAll().stream()
                .filter(s -> (semester == null || semester.isBlank() || semester.equalsIgnoreCase(s.getSemester())) &&
                             (academicYear == null || academicYear.isBlank() || academicYear.equalsIgnoreCase(s.getAcademicYear())))
                .map(classScheduleMapper::toClassScheduleResponse)
                .toList();
    }
}



