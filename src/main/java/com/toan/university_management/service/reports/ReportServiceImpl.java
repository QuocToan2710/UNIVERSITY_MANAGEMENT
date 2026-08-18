package com.toan.university_management.service.reports;


import com.toan.university_management.dto.reports.StudentReportRow;
import com.toan.university_management.entity.masterdata.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final Map<String, JasperReport> reportCache = new ConcurrentHashMap<>();

    @Override
    public byte[] exportHelloReport() {
        try {
            JasperReport report = getCompiledReport("reports/hello_report.jrxml");

            List<StudentReportRow> data = Arrays.asList(
                    new StudentReportRow("Nguyen Van A", "CNTT1", 85),
                    new StudentReportRow("Tran Thi B", "CNTT2", 90)
            );

            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(data);

            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    new HashMap<>(),
                    ds
            );

            return JasperExportManager.exportReportToPdf(print);

        } catch (Exception e) {
            log.error("Export hello report failed", e);
            throw new RuntimeException("Export report failed", e);
        }
    }

    private JasperReport getCompiledReport(String path) {
        return reportCache.computeIfAbsent(path, reportPath -> {
            try (InputStream template = new org.springframework.core.io.ClassPathResource(reportPath).getInputStream()) {
                log.info("Compiling JasperReport template once: {}", reportPath);
                return JasperCompileManager.compileReport(template);
            } catch (Exception e) {
                log.error("Error compiling JasperReport template: {}", reportPath, e);
                throw new IllegalStateException("Failed to compile JasperReport template: " + reportPath, e);
            }
        });
    }
}

