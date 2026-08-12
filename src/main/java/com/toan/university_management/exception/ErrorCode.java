package com.toan.university_management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(999,"Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001,"Invalid message key ", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002,"User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003,"Username must be at least {min} character", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004,"Password must be at least {min} character", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005,"User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006,"Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007,"You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008,"Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(1009,"Role not existed", HttpStatus.NOT_FOUND),
    DATA_INTEGRITY_VIOLATION(1010, "Data constraint violation or duplicate entry in database", HttpStatus.CONFLICT),
    INVALID_JSON_BODY(1011, "Malformed or invalid JSON request body", HttpStatus.BAD_REQUEST),
    INVALID_PARAM_TYPE(1012, "Invalid parameter type in request URL", HttpStatus.BAD_REQUEST),
    METHOD_NOT_SUPPORTED(1013, "HTTP method not supported for this endpoint", HttpStatus.METHOD_NOT_ALLOWED),
    STUDENT_NOT_FOUND(1014, "Student not found", HttpStatus.NOT_FOUND),
    TEACHER_NOT_FOUND(1015, "Teacher not found", HttpStatus.NOT_FOUND),
    COURSE_NOT_FOUND(1016, "Course not found", HttpStatus.NOT_FOUND),
    COURSE_EXISTED(1017, "Course/Subject already exists", HttpStatus.BAD_REQUEST),
    CLASS_GROUP_NOT_FOUND(2001, "Class group not found", HttpStatus.NOT_FOUND),
    CLASS_GROUP_EXISTED(2002, "Class group with this code already exists", HttpStatus.BAD_REQUEST),
    SCHEDULE_NOT_FOUND(2003, "Schedule not found", HttpStatus.NOT_FOUND),
    SCHEDULE_TEACHER_CONFLICT(2004, "Teacher already has a class scheduled at this time", HttpStatus.CONFLICT),
    SCHEDULE_ROOM_CONFLICT(2005, "Room is already occupied at this time", HttpStatus.CONFLICT),
    SCHEDULE_TIME_INVALID(2006, "End time must be after start time", HttpStatus.BAD_REQUEST),
    DEPARTMENT_NOT_FOUND(2007, "Department not found", HttpStatus.NOT_FOUND),
    MAJOR_NOT_FOUND(2008, "Major not found", HttpStatus.NOT_FOUND),
    BUILDING_NOT_FOUND(2009, "Building not found", HttpStatus.NOT_FOUND),
    ROOM_NOT_FOUND(2010, "Room not found", HttpStatus.NOT_FOUND),
    SUBJECT_NOT_FOUND(2011, "Subject not found", HttpStatus.NOT_FOUND)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
