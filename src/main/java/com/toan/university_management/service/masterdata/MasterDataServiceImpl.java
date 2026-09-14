package com.toan.university_management.service.masterdata;

import com.toan.university_management.model.masterdata.GetComboDataSourceInput;
import com.toan.university_management.model.masterdata.SelectOptionResponse;
import com.toan.university_management.entity.masterdata.*;
import com.toan.university_management.repository.masterdata.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.toan.university_management.enums.*;

import java.util.Arrays;
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

            case CLASS_GROUP_BY_MAJOR -> {
                if (cascader == null || cascader.isBlank()) {
                    yield List.of();
                }
                Long mId = parseOrFindMajorId(cascader);
                if (mId == null) {
                    yield List.of();
                }
                yield classGroupRepository.findAllByMajorIdAndDeletedFalse(mId).stream()
                        .map(c -> SelectOptionResponse.builder()
                                .value(useCodeAsId ? c.getClassCode() : String.valueOf(c.getId()))
                                .label(c.getClassName() + " (" + c.getClassCode() + ")")
                                .code(c.getClassCode())
                                .extra(String.valueOf(c.getMajorId()))
                                .build())
                        .toList();
            }

            case SUBJECT_CLASS, COURSE_CLASS -> subjectClassRepository.findAll().stream()
                    .map(sc -> SelectOptionResponse.builder()
                            .value(useCodeAsId ? sc.getSubjectClassCode() : String.valueOf(sc.getId()))
                            .label(sc.getName() + " (" + sc.getSubjectClassCode() + ")")
                            .code(sc.getSubjectClassCode())
                            .build())
                    .toList();

            case DEGREE -> Arrays.stream(AcademicDegree.values())
                    .map(AcademicDegree::toSelectOption)
                    .toList();

            case EXAM_FORMAT -> Arrays.stream(ExamFormat.values())
                    .map(ExamFormat::toSelectOption)
                    .toList();

            case ROOM_STATUS -> Arrays.stream(RoomStatus.values())
                    .map(RoomStatus::toSelectOption)
                    .toList();

            case ROOM_TYPE -> Arrays.stream(RoomType.values())
                    .map(RoomType::toSelectOption)
                    .toList();

            case STUDENT_STATUS -> Arrays.stream(StudentStatus.values())
                    .map(StudentStatus::toSelectOption)
                    .toList();

            case SEMESTER -> Arrays.stream(Semester.values())
                    .map(Semester::toSelectOption)
                    .toList();

            case ACADEMIC_YEAR -> {
                int currentYear = java.time.Year.now().getValue();
                yield java.util.stream.IntStream.rangeClosed(currentYear - 2, currentYear + 2)
                        .mapToObj(y -> {
                            String val = y + "-" + (y + 1);
                            return new SelectOptionResponse(val, val, val, null);
                        })
                        .toList();
            }

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

    private Long parseOrFindMajorId(String cascader) {
        if (cascader == null || cascader.isBlank()) return null;
        try {
            return Long.parseLong(cascader);
        } catch (NumberFormatException e) {
            return majorRepository.findByMajorCodeAndDeletedFalse(cascader)
                    .map(Major::getId)
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
