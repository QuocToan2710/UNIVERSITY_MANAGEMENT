package com.toan.university_management.service;

import com.toan.university_management.dto.request.CourseRequest;
import com.toan.university_management.dto.response.CourseResponse;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request);
    CourseResponse updateCourse(String id, CourseRequest request);
    void deleteCourse(String id);
    CourseResponse getCourseById(String id);
    List<CourseResponse> getAllCourses();

    List<CourseResponse> getCoursesByTeacherName(String teacherName);
}
