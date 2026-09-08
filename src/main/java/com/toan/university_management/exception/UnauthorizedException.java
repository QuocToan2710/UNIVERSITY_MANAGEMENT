package com.toan.university_management.exception;

/**
 * Exception thrown when authentication fails or is missing (HTTP 401).
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHENTICATED, message);
    }

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHENTICATED);
    }
}
