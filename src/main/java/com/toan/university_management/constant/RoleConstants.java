package com.toan.university_management.constant;

public final class RoleConstants {

    private RoleConstants() {
        // Prevent instantiation
    }

    // Spring Security Role Authorities (with ROLE_ prefix)
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_TEACHER = "ROLE_TEACHER";
    public static final String ROLE_STUDENT = "ROLE_STUDENT";

    // Base Role Names
    public static final String ADMIN = "ADMIN";
    public static final String TEACHER = "TEACHER";
    public static final String STUDENT = "STUDENT";

    // Descriptions
    public static final String ADMIN_DESC = "Quản trị viên hệ thống có toàn quyền";
    public static final String TEACHER_DESC = "Giảng viên phụ trách giảng dạy và chấm điểm";
    public static final String STUDENT_DESC = "Sinh viên tra cứu học tập, điểm số và thời khóa biểu";
}
