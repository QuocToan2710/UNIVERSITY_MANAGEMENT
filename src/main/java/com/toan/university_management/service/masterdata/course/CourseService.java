package com.toan.university_management.service.masterdata.course;

import com.toan.university_management.dto.request.masterdata.CourseRequest;
import com.toan.university_management.dto.response.masterdata.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    CourseResponse updateCourse(String id, CourseRequest request);
    void deleteCourse(String id);
    CourseResponse getCourseById(String id);
    List<CourseResponse> getAllCourses();
    Page<CourseResponse> getAllCourses(Pageable pageable);

    List<CourseResponse> getCoursesByTeacherName(String teacherName);
}


