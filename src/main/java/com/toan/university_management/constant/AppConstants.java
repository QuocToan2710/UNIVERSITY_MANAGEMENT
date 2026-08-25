package com.toan.university_management.constant;

public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // Pagination
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_PAGE_NUMBER_STR = "0";
    public static final String DEFAULT_PAGE_SIZE_STR = "10";
    public static final int MAX_PAGE_SIZE = 1000;

    // Academic & Semester Defaults
    public static final String DEFAULT_SEMESTER = "HK1";
    public static final String DEFAULT_ACADEMIC_YEAR = "2025-2026";

    // Authentication & Security
    public static final long OTP_EXPIRATION_MINUTES = 10;
    public static final String DEFAULT_PASSWORD_SUFFIX = "@123";
    public static final String UNIVERSITY_EMAIL_DOMAIN = "@university.edu.vn";
    public static final String OTP_CACHE_PREFIX = "OTP_RESET_PW:";

    // Date Time Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
