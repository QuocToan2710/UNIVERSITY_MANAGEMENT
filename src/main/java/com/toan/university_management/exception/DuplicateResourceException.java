package com.toan.university_management.exception;

import lombok.Getter;

/**
 * Exception thrown when creating or updating a resource violates uniqueness.
 * Maps to HTTP 400 BAD_REQUEST or 409 CONFLICT.
 */
@Getter
public class DuplicateResourceException extends BaseException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(ErrorCode.DATA_INTEGRITY_VIOLATION, String.format("%s already exists with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
