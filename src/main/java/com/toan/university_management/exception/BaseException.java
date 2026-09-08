package com.toan.university_management.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Root base exception for the entire application.
 * All domain-specific and business exceptions should extend this class.
 */
@Getter
@Setter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String customMessage;
    private Map<String, String> fieldErrors;

    public BaseException(ErrorCode errorCode) {
        super(errorCode != null ? errorCode.getMessage() : "Application Exception");
        this.errorCode = errorCode != null ? errorCode : ErrorCode.UNCATEGORIZED_EXCEPTION;
        this.customMessage = null;
    }

    public BaseException(ErrorCode errorCode, String customMessage) {
        super(customMessage != null ? customMessage : (errorCode != null ? errorCode.getMessage() : "Application Exception"));
        this.errorCode = errorCode != null ? errorCode : ErrorCode.UNCATEGORIZED_EXCEPTION;
        this.customMessage = customMessage;
    }

    public BaseException(ErrorCode errorCode, String customMessage, Map<String, String> fieldErrors) {
        super(customMessage != null ? customMessage : (errorCode != null ? errorCode.getMessage() : "Application Exception"));
        this.errorCode = errorCode != null ? errorCode : ErrorCode.UNCATEGORIZED_EXCEPTION;
        this.customMessage = customMessage;
        this.fieldErrors = fieldErrors;
    }

    public BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode != null ? errorCode.getMessage() : "Application Exception", cause);
        this.errorCode = errorCode != null ? errorCode : ErrorCode.UNCATEGORIZED_EXCEPTION;
        this.customMessage = null;
    }

    @Override
    public String getMessage() {
        if (customMessage != null && !customMessage.isBlank()) {
            return customMessage;
        }
        return errorCode != null ? errorCode.getMessage() : super.getMessage();
    }
}
