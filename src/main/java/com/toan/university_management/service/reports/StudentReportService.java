package com.toan.university_management.service.reports;

public interface StudentReportService {
    byte[] exportStudentReport() throws Exception;
    byte[] exportStudentReport(String format) throws Exception;
}
