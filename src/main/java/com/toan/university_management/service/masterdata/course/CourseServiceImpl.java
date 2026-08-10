package com.toan.university_management.service.masterdata.course;

import com.toan.university_management.dto.request.masterdata.CourseRequest;
import com.toan.university_management.dto.response.masterdata.CourseResponse;
import com.toan.university_management.entity.masterdata.Course;
import com.toan.university_management.entity.masterdata.Student;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.CourseMapper;
import com.toan.university_management.repository.masterdata.CourseRepository;
import com.toan.university_management.repository.masterdata.StudentRepository;
import com.toan.university_management.repository.masterdata.TeacherRepository;
import com.toan.university_management.service.masterdata.course.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {
    CourseRepository courseRepository;
    TeacherRepository teacherRepository;
    StudentRepository studentRepository;
    CourseMapper courseMapper;

    @Override
    public CourseResponse createCourse(CourseRequest request) {
        Course course = courseMapper.toCourse(request);

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        course.setTeacher(teacher);

        if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            List<Student> students = studentRepository.findAllById(request.getStudentIds());
            course.setStudents(students);
        }

        return courseMapper.toCourseResponse(courseRepository.save(course));
    }

    @Override
    public CourseResponse updateCourse(String id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        courseMapper.updateCourse(course, request);

        return courseMapper.toCourseResponse(courseRepository.save(course));
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteCourse(String id) {
        if (!courseRepository.existsById(id)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        courseRepository.deleteById(id);
    }

    @Override
    public CourseResponse getCourseById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return courseMapper.toCourseResponse(course);
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        return courseMapper.toCourseResponseList(courseRepository.findAll());
    }

    @Override
    public org.springframework.data.domain.Page<CourseResponse> getAllCourses(org.springframework.data.domain.Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(courseMapper::toCourseResponse);
    }

    @Override
    public List<CourseResponse> getCoursesByTeacherName(String teacherName) {
        List<Course> courses = courseRepository.findByTeacherNameWithTeacherInfo(teacherName);
        return courseMapper.toCourseResponseList(courses);
    }
}


