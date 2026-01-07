package com.toan.university_management.reportmock;

import com.toan.university_management.dto.reports.StudentReportRow;

import java.util.List;

public class StudentReportMock {
    public static List<StudentReportRow> data() {
        return List.of(
                new StudentReportRow("Nguyễn Văn A", "CNTT1", 85),
                new StudentReportRow("Trần Thị B", "CNTT2", 90),
                new StudentReportRow("Lê Văn C", "CNTT1", 78)
        );
    }
}
