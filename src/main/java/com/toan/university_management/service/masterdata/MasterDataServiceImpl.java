package com.toan.university_management.service.masterdata;

import com.toan.university_management.dto.request.masterdata.GetComboDataSourceInput;
import com.toan.university_management.dto.response.masterdata.SelectOptionResponse;
import com.toan.university_management.entity.masterdata.*;
import com.toan.university_management.repository.masterdata.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class MasterDataServiceImpl implements MasterDataService {
    DepartmentRepository departmentRepository;
    MajorRepository majorRepository;
    BuildingRepository buildingRepository;
    FloorRepository floorRepository;
    RoomRepository roomRepository;
    SubjectRepository subjectRepository;
    TeacherRepository teacherRepository;
    ClassGroupRepository classGroupRepository;
    StudentRepository studentRepository;
    SubjectClassRepository subjectClassRepository;
    ProvinceRepository provinceRepository;
    DistrictRepository districtRepository;
    WardRepository wardRepository;

    @Override
    public List<SelectOptionResponse> getByType(GetComboDataSourceInput input) {
        if (input == null || input.getType() == null) return Collections.emptyList();

        boolean useCodeAsId = Boolean.TRUE.equals(input.getIsCodeIsId());
        String cascader = input.getCascader();

        return switch (input.getType()) {
            case DEPARTMENT -> departmentRepository.findAll().stream()
                    .map(d -> SelectOptionResponse.builder()
                            .value(useCodeAsId ? d.getDepartmentCode() : String.valueOf(d.getId()))
                            .label(d.getName() + " (" + d.getDepartmentCode() + ")")
                            .code(d.getDepartmentCode())
                            .build())
                    .toList();

            case MAJOR -> {
                var stream = majorRepository.findAll().stream();
                if (cascader != null && !cascader.isBlank()) {
                    Long deptId = parseOrFindDepartmentId(cascader);
                    if (deptId != null) {
                        stream = stream.filter(m -> deptId.equals(m.getDepartmentId()));
                    }
                }
                yield stream.map(m -> SelectOptionResponse.builder()
                                .value(useCodeAsId ? m.getMajorCode() : String.valueOf(m.getId()))
                                .label(m.getName() + " (" + m.getMajorCode() + ")")
                                .code(m.getMajorCode())
                                .extra(m.getDepartmentId() != null ? String.valueOf(m.getDepartmentId()) : null)
                                .build())
                        .toList();
            }

            case BUILDING -> buildingRepository.findAll().stream()
                    .map(b -> SelectOptionResponse.builder()
                            .value(useCodeAsId ? b.getBuildingCode() : String.valueOf(b.getId()))
                            .label(b.getName() + " (" + b.getBuildingCode() + ")")
                            .code(b.getBuildingCode())
                            .build())
                    .toList();

            case FLOOR -> {
                var stream = floorRepository.findAll().stream();
                if (cascader != null && !cascader.isBlank()) {
                    Long bldgId = parseOrFindBuildingId(cascader);
                    if (bldgId != null) {
                        stream = stream.filter(f -> bldgId.equals(f.getBuildingId()));
                    }
                }
                yield stream.map(f -> SelectOptionResponse.builder()
                                .value(useCodeAsId ? f.getFloorCode() : String.valueOf(f.getId()))
                                .label(f.getName() + " (" + f.getFloorCode() + ")")
                                .code(f.getFloorCode())
                                .extra(f.getBuildingId() != null ? String.valueOf(f.getBuildingId()) : null)
                                .build())
                        .toList();
            }

            case ROOM -> roomRepository.findAll().stream()
                    .map(r -> SelectOptionResponse.builder()
                            .value(useCodeAsId ? r.getRoomCode() : String.valueOf(r.getId()))
                            .label(r.getName() + " [" + r.getBuilding() + "]")
                            .code(r.getRoomCode())
                            .extra(r.getStatus())
                            .build())
                    .toList();

            case SUBJECT -> subjectRepository.findAll().stream()
                    .map(s -> SelectOptionResponse.builder()
                            .value(useCodeAsId ? s.getSubjectCode() : String.valueOf(s.getId()))
                            .label(s.getName() + " (" + s.getSubjectCode() + ")")
                            .code(s.getSubjectCode())
                            .build())
                    .toList();

            case TEACHER -> {
                var stream = teacherRepository.findAll().stream();
                if (cascader != null && !cascader.isBlank()) {
                    Long deptId = parseOrFindDepartmentId(cascader);
                    if (deptId != null) {
                        stream = stream.filter(t -> deptId.equals(t.getDepartmentId()));
                    }
                }
                yield stream.map(t -> SelectOptionResponse.builder()
                                .value(useCodeAsId ? t.getTeacherCode() : String.valueOf(t.getId()))
                                .label(t.getFullName() + (t.getDegree() != null ? " - " + t.getDegree() : ""))
                                .code(t.getTeacherCode())
                                .extra(t.getDepartmentId() != null ? String.valueOf(t.getDepartmentId()) : null)
                                .build())
                        .toList();
            }

            case STUDENT -> studentRepository.findAll().stream()
                    .map(s -> SelectOptionResponse.builder()
                            .value(useCodeAsId ? s.getStudentCode() : String.valueOf(s.getId()))
                            .label(s.getFullName() + " (" + s.getStudentCode() + ")")
                            .code(s.getStudentCode())
                            .extra(s.getClassGroupId() != null ? String.valueOf(s.getClassGroupId()) : null)
                            .build())
                    .toList();

            case CLASS_GROUP -> classGroupRepository.findAll().stream()
                    .map(c -> SelectOptionResponse.builder()
                            .value(useCodeAsId ? c.getClassCode() : String.valueOf(c.getId()))
                            .label(c.getClassName() + " (" + c.getClassCode() + ")")
                            .code(c.getClassCode())
                            .build())
                    .toList();

            case SUBJECT_CLASS, COURSE_CLASS -> subjectClassRepository.findAll().stream()
                    .map(sc -> SelectOptionResponse.builder()
                            .value(useCodeAsId ? sc.getSubjectClassCode() : String.valueOf(sc.getId()))
                            .label(sc.getName() + " (" + sc.getSubjectClassCode() + ")")
                            .code(sc.getSubjectClassCode())
                            .build())
                    .toList();

            case DEGREE -> List.of(
                    new SelectOptionResponse("Tiến sĩ", "Tiến sĩ (TS)", "TS", null),
                    new SelectOptionResponse("Thạc sĩ", "Thạc sĩ (ThS)", "ThS", null),
                    new SelectOptionResponse("Phó Giáo sư", "Phó Giáo sư (PGS)", "PGS", null),
                    new SelectOptionResponse("Giáo sư", "Giáo sư (GS)", "GS", null),
                    new SelectOptionResponse("Cử nhân", "Cử nhân (CN)", "CN", null),
                    new SelectOptionResponse("Kỹ sư", "Kỹ sư (KS)", "KS", null)
            );

            case EXAM_FORMAT -> List.of(
                    new SelectOptionResponse("Tự luận", "Tự luận", "WRITTEN", null),
                    new SelectOptionResponse("Trắc nghiệm", "Trắc nghiệm", "MULTIPLE_CHOICE", null),
                    new SelectOptionResponse("Thực hành", "Thực hành", "PRACTICAL", null),
                    new SelectOptionResponse("Báo cáo đồ án", "Báo cáo đồ án", "PROJECT", null)
            );

            case ROOM_STATUS -> List.of(
                    new SelectOptionResponse("ACTIVE", "Hoạt động", "ACTIVE", null),
                    new SelectOptionResponse("MAINTENANCE", "Đang bảo trì", "MAINTENANCE", null),
                    new SelectOptionResponse("INACTIVE", "Tạm khóa", "INACTIVE", null)
            );

            case ROOM_TYPE -> List.of(
                    new SelectOptionResponse("Giảng đường", "Giảng đường", "LECTURE_HALL", null),
                    new SelectOptionResponse("Phòng máy tính", "Phòng máy tính", "COMPUTER_LAB", null),
                    new SelectOptionResponse("Phòng thí nghiệm", "Phòng thí nghiệm", "LAB", null),
                    new SelectOptionResponse("Hội trường", "Hội trường", "AUDITORIUM", null),
                    new SelectOptionResponse("Phòng thực hành", "Phòng thực hành", "PRACTICE_ROOM", null)
            );

            case STUDENT_STATUS -> List.of(
                    new SelectOptionResponse("ACTIVE", "Đang học", "ACTIVE", null),
                    new SelectOptionResponse("GRADUATED", "Đã tốt nghiệp", "GRADUATED", null),
                    new SelectOptionResponse("SUSPENDED", "Bảo lưu kết quả", "SUSPENDED", null),
                    new SelectOptionResponse("DROPPED", "Thôi học", "DROPPED", null)
            );

            case SEMESTER -> List.of(
                    new SelectOptionResponse("1", "Học kỳ 1", "HK1", null),
                    new SelectOptionResponse("2", "Học kỳ 2", "HK2", null),
                    new SelectOptionResponse("3", "Học kỳ Hè", "HK3", null)
            );

            case ACADEMIC_YEAR -> List.of(
                    new SelectOptionResponse("2024-2025", "2024 - 2025", "2024-2025", null),
                    new SelectOptionResponse("2025-2026", "2025 - 2026", "2025-2026", null),
                    new SelectOptionResponse("2023-2024", "2023 - 2024", "2023-2024", null)
            );

            case PROVINCE -> provinceRepository.findAllByDeletedFalseOrderByProvinceNameAsc().stream()
                    .map(p -> SelectOptionResponse.builder()
                            .value(useCodeAsId ? p.getProvinceCode() : String.valueOf(p.getId()))
                            .label(p.getProvinceName())
                            .code(p.getProvinceCode())
                            .extra(p.getProvinceType())
                            .build())
                    .toList();

            case DISTRICT -> {
                var stream = districtRepository.findAllByDeletedFalseOrderByDistrictNameAsc().stream();
                if (cascader != null && !cascader.isBlank()) {
                    Long provId = parseOrFindProvinceId(cascader);
                    if (provId != null) {
                        stream = stream.filter(d -> provId.equals(d.getProvinceId()));
                    }
                }
                yield stream.map(d -> SelectOptionResponse.builder()
                                .value(useCodeAsId ? d.getDistrictCode() : String.valueOf(d.getId()))
                                .label(d.getDistrictName())
                                .code(d.getDistrictCode())
                                .extra(d.getProvinceId() != null ? String.valueOf(d.getProvinceId()) : null)
                                .build())
                        .toList();
            }

            case WARD -> {
                var stream = wardRepository.findAllByDeletedFalseOrderByWardNameAsc().stream();
                if (cascader != null && !cascader.isBlank()) {
                    Long distId = parseOrFindDistrictId(cascader);
                    if (distId != null) {
                        stream = stream.filter(w -> distId.equals(w.getDistrictId()));
                    }
                }
                yield stream.map(w -> SelectOptionResponse.builder()
                                .value(useCodeAsId ? w.getWardCode() : String.valueOf(w.getId()))
                                .label(w.getWardName())
                                .code(w.getWardCode())
                                .extra(w.getDistrictId() != null ? String.valueOf(w.getDistrictId()) : null)
                                .build())
                        .toList();
            }
        };
    }

    private Long parseOrFindDepartmentId(String cascader) {
        if (cascader == null || cascader.isBlank()) return null;
        try {
            return Long.parseLong(cascader);
        } catch (NumberFormatException e) {
            return departmentRepository.findByDepartmentCodeAndDeletedFalse(cascader)
                    .map(Department::getId)
                    .orElse(null);
        }
    }

    private Long parseOrFindBuildingId(String cascader) {
        if (cascader == null || cascader.isBlank()) return null;
        try {
            return Long.parseLong(cascader);
        } catch (NumberFormatException e) {
            return buildingRepository.findByBuildingCodeAndDeletedFalse(cascader)
                    .map(Building::getId)
                    .orElse(null);
        }
    }

    private Long parseOrFindProvinceId(String cascader) {
        if (cascader == null || cascader.isBlank()) return null;
        try {
            return Long.parseLong(cascader);
        } catch (NumberFormatException e) {
            return provinceRepository.findByProvinceCodeAndDeletedFalse(cascader)
                    .map(Province::getId)
                    .orElse(null);
        }
    }

    private Long parseOrFindDistrictId(String cascader) {
        if (cascader == null || cascader.isBlank()) return null;
        try {
            return Long.parseLong(cascader);
        } catch (NumberFormatException e) {
            return districtRepository.findByDistrictCodeAndDeletedFalse(cascader)
                    .map(District::getId)
                    .orElse(null);
        }
    }
}
