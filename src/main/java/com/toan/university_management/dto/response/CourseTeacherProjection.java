package com.toan.university_management.dto.response;

public interface CourseTeacherProjection {
    String getId();
    String getCourseCode();
    String getCourseName();
    Integer getCredit();
    String getSemester();

    String getTeacherId();
    String getTeacherCode();
    String getTeacherName();
    String getTeacherEmail();
    String getTeacherPhone();
    String getTeacherSpecialization();
}
