package com.toan.university_management.service.reports;

import com.toan.university_management.dto.reports.StudentReportDTO;
import com.toan.university_management.repository.masterdata.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentReportServiceImpl implements StudentReportService {

    private final StudentRepository studentRepository;
    private final Map<String, JasperReport> reportCache = new ConcurrentHashMap<>();

    @Override
    public byte[] exportStudentReport() throws Exception {
        // Method mặc định gọi method có parameter với format PDF
        return exportStudentReport("PDF");
    }

    @Override
    public byte[] exportStudentReport(String format) throws Exception {
        List<StudentReportDTO> students = studentRepository.getAllStudentForReport();

        JasperReport jasperReport = getCompiledReport("reports/information_student.jrxml");

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students);

        Map<String, Object> params = new HashMap<>();
        params.put("createdDate", new Date());

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                        jasperReport,
                        params,
                        dataSource
                );

        if ("EXCEL".equalsIgnoreCase(format) || "XLSX".equalsIgnoreCase(format)) {
            return exportToExcel(jasperPrint);
        } else {
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }

    private JasperReport getCompiledReport(String path) {
        return reportCache.computeIfAbsent(path, reportPath -> {
            try (InputStream template = new ClassPathResource(reportPath).getInputStream()) {
                log.info("Compiling JasperReport template once: {}", reportPath);
                return JasperCompileManager.compileReport(template);
            } catch (Exception e) {
                log.error("Error compiling JasperReport template: {}", reportPath, e);
                throw new IllegalStateException("Failed to compile JasperReport template: " + reportPath, e);
            }
        });
    }

    // Method private để export Excel
    private byte[] exportToExcel(JasperPrint jasperPrint) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        configuration.setWhitePageBackground(false);
        configuration.setRemoveEmptySpaceBetweenRows(true);
        configuration.setOnePagePerSheet(false);

        exporter.setConfiguration(configuration);
        exporter.exportReport();

        return outputStream.toByteArray();
    }
}
