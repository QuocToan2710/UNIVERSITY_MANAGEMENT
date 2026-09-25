package com.toan.university_management;

import com.toan.university_management.entity.masterdata.*;
import com.toan.university_management.enums.TuitionStatus;
import com.toan.university_management.model.masterdata.DistrictResponse;
import com.toan.university_management.model.masterdata.StudentTuitionSummaryResponse;
import com.toan.university_management.model.masterdata.TuitionDashboardSummaryResponse;
import com.toan.university_management.model.masterdata.WardResponse;
import com.toan.university_management.repository.masterdata.*;
import com.toan.university_management.service.masterdata.district.DistrictService;
import com.toan.university_management.service.masterdata.tuition.TuitionService;
import com.toan.university_management.service.masterdata.ward.WardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TuitionAndLocationPaginationTest {

    @Autowired
    DistrictService districtService;

    @Autowired
    WardService wardService;

    @Autowired
    TuitionService tuitionService;

    @Autowired
    ProvinceRepository provinceRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    WardRepository wardRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SubjectClassRepository subjectClassRepository;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    TuitionFeeRepository tuitionFeeRepository;

    private Province province;
    private District district1;
    private District district2;

    @BeforeEach
    void setUp() {
        String suffix = String.valueOf(System.nanoTime());

        province = provinceRepository.save(Province.builder()
                .provinceCode("P_" + suffix)
                .provinceName("Province " + suffix)
                .provinceType("Tỉnh")
                .build());

        district1 = districtRepository.save(District.builder()
                .districtCode("D1_" + suffix)
                .districtName("District Alpha " + suffix)
                .districtType("Quận")
                .provinceId(province.getId())
                .build());

        district2 = districtRepository.save(District.builder()
                .districtCode("D2_" + suffix)
                .districtName("District Beta " + suffix)
                .districtType("Quận")
                .provinceId(province.getId())
                .build());

        wardRepository.save(Ward.builder()
                .wardCode("W1_" + suffix)
                .wardName("Ward 01 " + suffix)
                .wardType("Phường")
                .districtId(district1.getId())
                .build());

        wardRepository.save(Ward.builder()
                .wardCode("W2_" + suffix)
                .wardName("Ward 02 " + suffix)
                .wardType("Phường")
                .districtId(district1.getId())
                .build());
    }

    @Test
    @DisplayName("District pagination returns correct page elements and enriched province name")
    void testDistrictPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DistrictResponse> page = districtService.getAllDistricts(province.getId(), pageable);

        assertNotNull(page);
        assertTrue(page.getTotalElements() >= 2);
        assertTrue(page.getContent().stream().anyMatch(d -> d.getDistrictCode().equals(district1.getDistrictCode())));
        assertEquals(province.getProvinceName(), page.getContent().get(0).getProvinceName());
    }

    @Test
    @DisplayName("Ward pagination returns correct page elements and enriched district name")
    void testWardPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WardResponse> page = wardService.getAllWards(district1.getId(), pageable);

        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
        assertEquals(district1.getDistrictName(), page.getContent().get(0).getDistrictName());
    }

    @Test
    @DisplayName("Tuition batch pagination calculates correct credits, amounts, and handles filtering")
    void testTuitionBatchPagination() {
        String suffix = String.valueOf(System.nanoTime());

        Student stu = studentRepository.save(Student.builder()
                .studentCode("STU_TUITION_" + suffix)
                .fullName("Sinh Vien Tuition " + suffix)
                .email("tuition_" + suffix + "@test.edu.vn")
                .build());

        Subject sub = subjectRepository.save(Subject.builder()
                .subjectCode("SUB_TUI_" + suffix)
                .name("Mon Hoc Tuition " + suffix)
                .credit(4)
                .build());

        SubjectClass sc = subjectClassRepository.save(SubjectClass.builder()
                .subjectClassCode("SC_TUI_" + suffix)
                .name("Lop Hoc Phan Tuition " + suffix)
                .subjectId(sub.getId())
                .semester("1")
                .academicYear("2025-2026")
                .maxCapacity(50)
                .build());

        enrollmentRepository.save(Enrollment.builder()
                .enrollmentCode("ENR_TUI_" + suffix)
                .studentId(stu.getId())
                .subjectClassId(sc.getId())
                .enrolledAt(LocalDateTime.now())
                .build());

        Pageable pageable = PageRequest.of(0, 10);
        Page<StudentTuitionSummaryResponse> page = tuitionService.getAllStudentsTuition(
                "1", "2025-2026", null, null, stu.getStudentCode(), pageable);

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        StudentTuitionSummaryResponse summary = page.getContent().get(0);
        assertEquals(stu.getStudentCode(), summary.getStudentCode());
        assertEquals(4, summary.getTotalCredits());
        assertEquals(4 * 450000L, summary.getTotalAmount());
        assertEquals(TuitionStatus.UNPAID, summary.getStatus());

        // Test Dashboard Summary
        TuitionDashboardSummaryResponse dashboard = tuitionService.getDashboardSummary("1", "2025-2026");
        assertNotNull(dashboard);
        assertTrue(dashboard.getTotalCreditsEnrolled() >= 4);
        assertTrue(dashboard.getTotalTuitionExpected() >= 4 * 450000L);
    }
}
