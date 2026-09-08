package com.toan.university_management.exception;

public class AppException extends BaseException {

    public AppException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }
}
