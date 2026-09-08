package com.toan.university_management.exception;

/**
 * Exception thrown when user is authenticated but lacks required permission (HTTP 403).
 */
public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }

    public ForbiddenException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
