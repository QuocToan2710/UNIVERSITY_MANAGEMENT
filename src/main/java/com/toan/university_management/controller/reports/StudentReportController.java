package com.toan.university_management.controller.reports;

import com.toan.university_management.service.reports.StudentReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentReportController {

    @Autowired
    StudentReportService studentReportService;


    @GetMapping("/students")
    public ResponseEntity<byte[]> exportStudentReport(
            @RequestParam(defaultValue = "PDF") String format) throws Exception {

        log.info("Exporting student report in format: {}", format);

        byte[] report = studentReportService.exportStudentReport(format);

        HttpHeaders headers = new HttpHeaders();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename;

        if ("EXCEL".equalsIgnoreCase(format) || "XLSX".equalsIgnoreCase(format)) {
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            filename = "student_list_" + timestamp + ".xlsx";
            log.info("Generated Excel report: {}", filename);
        } else {
            headers.setContentType(MediaType.APPLICATION_PDF);
            filename = "student_list_" + timestamp + ".pdf";
            log.info("Generated PDF report: {}", filename);
        }

        headers.setContentDisposition(ContentDisposition
                        .attachment() // Dùng attachment để tự động download
                        .filename(filename)
                        .build()
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(report);
    }
}