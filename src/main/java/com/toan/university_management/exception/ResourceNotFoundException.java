package com.toan.university_management.exception;

import lombok.Getter;

/**
 * Exception thrown when a requested resource is not found in database.
 * Maps to HTTP 404 NOT_FOUND.
 */
@Getter
public class ResourceNotFoundException extends BaseException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundException(String resourceName) {
        super(ErrorCode.RESOURCE_NOT_FOUND, String.format("%s not found", resourceName));
        this.resourceName = resourceName;
        this.fieldName = "id";
        this.fieldValue = null;
    }
}
