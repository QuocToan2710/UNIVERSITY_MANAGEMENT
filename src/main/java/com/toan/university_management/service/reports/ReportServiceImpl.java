package com.toan.university_management.service.reports;


import com.toan.university_management.dto.reports.StudentReportRow;
import com.toan.university_management.entity.Student;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{


    @Override
    public byte[] exportHelloReport() {
        try {
            // 1. Load JRXML
            InputStream jrxml = getClass()
                    .getResourceAsStream("/reports/hello_report.jrxml");

            JasperReport report =
                    JasperCompileManager.compileReport(jrxml);

            // 2. Mock data
            List<StudentReportRow> data = Arrays.asList(
                    new StudentReportRow("Nguyen Van A", "CNTT1", 85),
                    new StudentReportRow("Tran Thi B", "CNTT2", 90)
            );

            JRBeanCollectionDataSource ds =
                    new JRBeanCollectionDataSource(data);

            // 3. Fill report
            JasperPrint print = JasperFillManager.fillReport(
                    report,
                    new HashMap<>(),
                    ds
            );

            // 4. Export PDF
            return JasperExportManager.exportReportToPdf(print);

        } catch (Exception e) {
            throw new RuntimeException("Export report failed", e);
        }
    }
}
