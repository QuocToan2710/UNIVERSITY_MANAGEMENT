package com.toan.university_management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/**
 * Enterprise standardized ErrorCode enum containing:
 * - code: Numerical business code (e.g. 1001, 2018)
 * - message: Standardized error code string for FE i18n lookup (e.g. "error.student.email_existed")
 * - statusCode: HTTP status code
 */
@Getter
public enum ErrorCode {

    // === System & Common (999, 1001, 1010-1013, 1022) ===
    UNCATEGORIZED_EXCEPTION(999, "error.common.uncategorized", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "error.common.invalid.key", HttpStatus.BAD_REQUEST),
    DATA_INTEGRITY_VIOLATION(1010, "error.common.data.integrity.violation", HttpStatus.CONFLICT),
    INVALID_JSON_BODY(1011, "error.common.invalid.json.body", HttpStatus.BAD_REQUEST),
    INVALID_PARAM_TYPE(1012, "error.common.invalid.param.type", HttpStatus.BAD_REQUEST),
    METHOD_NOT_SUPPORTED(1013, "error.common.method.not.supported", HttpStatus.METHOD_NOT_ALLOWED),
    RESOURCE_NOT_FOUND(1022, "error.common.resource.not.found", HttpStatus.NOT_FOUND),

    // === Authentication & Security (1006-1007, 1019-1020, 1025) ===
    UNAUTHENTICATED(1006, "error.auth.unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "error.auth.forbidden", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(1025, "error.auth.invalid.credentials", HttpStatus.BAD_REQUEST),
    OTP_INVALID(1019, "error.auth.otp.invalid", HttpStatus.BAD_REQUEST),
    OTP_EXPIRED(1020, "error.auth.otp.expired", HttpStatus.BAD_REQUEST),

    // === User & Account (1002-1005, 1008-1009, 1018, 1021, 1023) ===
    USER_EXISTED(1002, "error.user.already.exists", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "error.user.username.invalid", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "error.user.password.invalid", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "error.user.not.found", HttpStatus.NOT_FOUND),
    INVALID_DOB(1008, "error.user.invalid.dob", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1009, "error.role.not.found", HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND(1018, "error.email.not.found", HttpStatus.NOT_FOUND),
    EMAIL_SEND_FAILED(1021, "error.email.send.failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_EXISTED(1023, "error.email.already.exists", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT(1026, "error.user.old_password.incorrect", HttpStatus.BAD_REQUEST),
    PASSWORD_SAME_AS_OLD(1027, "error.user.password.same_as_old", HttpStatus.BAD_REQUEST),
    PASSWORD_CONFIRM_NOT_MATCH(1028, "error.user.password.confirm_not_match", HttpStatus.BAD_REQUEST),

    // === Master Data: Student, Teacher, Course
    STUDENT_NOT_FOUND(1014, "error.student.not.found", HttpStatus.NOT_FOUND),
    TEACHER_NOT_FOUND(1015, "error.teacher.not_found", HttpStatus.NOT_FOUND),
    COURSE_NOT_FOUND(1016, "error.course.not.found", HttpStatus.NOT_FOUND),
    COURSE_EXISTED(1017, "error.course.already.exists", HttpStatus.BAD_REQUEST),
    CLASS_GROUP_NOT_FOUND(2001, "error.class.group.not.found", HttpStatus.NOT_FOUND),
    CLASS_GROUP_EXISTED(2002, "error.class.group.already.exists", HttpStatus.BAD_REQUEST),
    DEPARTMENT_NOT_FOUND(2007, "error.department.not.found", HttpStatus.NOT_FOUND),
    MAJOR_NOT_FOUND(2008, "error.major.not.found", HttpStatus.NOT_FOUND),
    BUILDING_NOT_FOUND(2009, "error.building.not.found", HttpStatus.NOT_FOUND),
    ROOM_NOT_FOUND(2010, "error.room.not.found", HttpStatus.NOT_FOUND),
    SUBJECT_NOT_FOUND(2011, "error.subject.not.found", HttpStatus.NOT_FOUND),
    SUBJECT_CLASS_NOT_FOUND(2014, "error.subject.class.not.found", HttpStatus.NOT_FOUND),
    SUBJECT_CLASS_EXISTED(2024, "error.subject.class.already.exists", HttpStatus.BAD_REQUEST),
    PROVINCE_NOT_FOUND(2015, "error.province.not.found", HttpStatus.NOT_FOUND),
    DISTRICT_NOT_FOUND(2016, "error.district.not.found", HttpStatus.NOT_FOUND),
    WARD_NOT_FOUND(2017, "error.ward.not.found", HttpStatus.NOT_FOUND),

    // === Schedule & Timetable (2003-2006) ===
    SCHEDULE_NOT_FOUND(2003, "error.schedule.not.found", HttpStatus.NOT_FOUND),
    SCHEDULE_TEACHER_CONFLICT(2004, "error.schedule.teacher.conflict", HttpStatus.CONFLICT),
    SCHEDULE_ROOM_CONFLICT(2005, "error.schedule.room.conflict", HttpStatus.CONFLICT),
    SCHEDULE_TIME_INVALID(2006, "error.schedule.time.invalid", HttpStatus.BAD_REQUEST),

    // === Course Registration & Enrollment (2012-2013, 2018-2019, 2025-2027) ===
    ENROLLMENT_NOT_FOUND(2012, "error.enrollment.not.found", HttpStatus.NOT_FOUND),
    ENROLLMENT_ALREADY_EXISTS(2013, "error.enrollment.already.exists", HttpStatus.BAD_REQUEST),
    ENROLLMENT_CAPACITY_FULL(2018, "error.enrollment.capacity.full", HttpStatus.BAD_REQUEST),
    ENROLLMENT_SCHEDULE_CONFLICT(2019, "error.enrollment.schedule.conflict", HttpStatus.CONFLICT),
    ENROLLMENT_CANNOT_BE_CANCELLED(2025, "error.enrollment.cannot.be.cancelled", HttpStatus.BAD_REQUEST),
    ENROLLMENT_MAX_CREDITS_EXCEEDED(2026, "error.enrollment.max.credits.exceeded", HttpStatus.BAD_REQUEST),
    ENROLLMENT_SUBJECT_ALREADY_REGISTERED(2027, "error.enrollment.subject.already.registered", HttpStatus.BAD_REQUEST),

    // === Grades & Examination (2020-2022) ===
    GRADE_LOCKED(2020, "error.grade.locked", HttpStatus.BAD_REQUEST),
    GRADE_INVALID_SCORE(2021, "error.grade.invalid.score", HttpStatus.BAD_REQUEST),
    GRADE_NOT_SUBMITTED(2022, "error.grade.not.submitted", HttpStatus.BAD_REQUEST),

    // === Attendance (2023) ===
    ATTENDANCE_SESSION_NOT_FOUND(2023, "error.attendance.session.not.found", HttpStatus.NOT_FOUND)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
