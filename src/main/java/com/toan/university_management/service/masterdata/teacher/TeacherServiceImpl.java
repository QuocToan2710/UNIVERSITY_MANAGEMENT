package com.toan.university_management.service.masterdata.teacher;

import com.toan.university_management.dto.request.masterdata.TeacherRequest;
import com.toan.university_management.dto.response.masterdata.TeacherResponse;
import com.toan.university_management.entity.masterdata.Department;
import com.toan.university_management.entity.masterdata.District;
import com.toan.university_management.entity.masterdata.Province;
import com.toan.university_management.entity.masterdata.Teacher;
import com.toan.university_management.entity.masterdata.Ward;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.mapper.masterdata.TeacherMapper;
import com.toan.university_management.repository.masterdata.DepartmentRepository;
import com.toan.university_management.repository.masterdata.DistrictRepository;
import com.toan.university_management.repository.masterdata.ProvinceRepository;
import com.toan.university_management.repository.masterdata.TeacherRepository;
import com.toan.university_management.repository.masterdata.WardRepository;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.identity.UserRole;
import com.toan.university_management.repository.identity.RoleRepository;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.identity.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class TeacherServiceImpl implements TeacherService {
    TeacherRepository teacherRepository;
    DepartmentRepository departmentRepository;
    ProvinceRepository provinceRepository;
    DistrictRepository districtRepository;
    WardRepository wardRepository;
    TeacherMapper teacherMapper;
    UserRepository userRepository;
    UserRoleRepository userRoleRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    com.toan.university_management.service.email.EmailService emailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeacherResponse createTeacher(TeacherRequest request) {
        if (teacherRepository.existsByTeacherCodeAndDeletedFalse(request.getTeacherCode())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (request.getDepartmentId() != null && !departmentRepository.existsByIdAndDeletedFalse(request.getDepartmentId())) {
            throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        Teacher teacher = teacherMapper.toTeacher(request);

        // Đảm bảo Email luôn được gán chính xác từ hồ sơ giảng viên
        String email = (teacher.getEmail() != null && !teacher.getEmail().isBlank())
                ? teacher.getEmail().trim()
                : teacher.getTeacherCode().toLowerCase() + "@university.edu.vn";
        teacher.setEmail(email);

        if (teacher.getUserId() == null) {
            var userOpt = userRepository.findByUsername(teacher.getTeacherCode())
                    .or(() -> userRepository.findByEmail(email));

            if (userOpt.isPresent()) {
                teacher.setUserId(userOpt.get().getId());
            } else {
                // Tự động cấp tài khoản đăng nhập cho giảng viên
                String rawPassword = teacher.getTeacherCode() + "@123";

                User newUser = User.builder()
                        .username(teacher.getTeacherCode())
                        .password(passwordEncoder.encode(rawPassword))
                        .email(email)
                        .fullName(teacher.getFullName())
                        .userCode(teacher.getTeacherCode())
                        .build();
                newUser = userRepository.save(newUser);

                final Long newUserId = newUser.getId();
                roleRepository.findByRoleCode("ROLE_TEACHER")
                        .or(() -> roleRepository.findByName("TEACHER"))
                        .ifPresent(role -> {
                            userRoleRepository.save(UserRole.builder()
                                    .userId(newUserId)
                                    .roleId(role.getId())
                                    .build());
                        });

                teacher.setUserId(newUserId);
                log.info("Auto-created User account for teacher {}: email={}, username={}, defaultPassword={}", teacher.getTeacherCode(), email, teacher.getTeacherCode(), rawPassword);

                // Gửi email thông báo tài khoản & mật khẩu ban đầu
                emailService.sendAccountCreatedEmail(email, teacher.getFullName(), teacher.getTeacherCode(), rawPassword, "ROLE_TEACHER");
            }
        }

        teacher = teacherRepository.save(teacher);
        return enrichResponse(teacher);
    }

    @Override
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        return enrichResponse(teacher);
    }

    @Override
    public List<TeacherResponse> getAllTeachers() {
        return enrichResponses(teacherRepository.findAllByDeletedFalse());
    }

    @Override
    public Page<TeacherResponse> getAllTeachers(Pageable pageable) {
        Page<Teacher> page = teacherRepository.findAllByDeletedFalse(pageable);
        List<TeacherResponse> content = enrichResponses(page.getContent());
        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeacherResponse updateTeacher(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        if (request.getDepartmentId() != null && !departmentRepository.existsByIdAndDeletedFalse(request.getDepartmentId())) {
            throw new AppException(ErrorCode.DEPARTMENT_NOT_FOUND);
        }
        teacherMapper.updateTeacher(teacher, request);

        // Đồng bộ email và họ tên sang tài khoản User nếu có
        if (teacher.getUserId() != null) {
            final String updatedEmail = teacher.getEmail();
            final String updatedName = teacher.getFullName();
            userRepository.findByIdAndDeletedFalse(teacher.getUserId()).ifPresent(u -> {
                if (updatedEmail != null && !updatedEmail.isBlank()) {
                    u.setEmail(updatedEmail.trim());
                }
                if (updatedName != null && !updatedName.isBlank()) {
                    u.setFullName(updatedName);
                }
                userRepository.save(u);
            });
        }

        teacher = teacherRepository.save(teacher);
        return enrichResponse(teacher);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));
        teacher.setDeleted(true);
        if (teacher.getUserId() != null) {
            userRepository.findByIdAndDeletedFalse(teacher.getUserId()).ifPresent(u -> {
                u.setDeleted(true);
                userRepository.save(u);
            });
        }
        teacherRepository.save(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public com.toan.university_management.dto.response.BasePaginationRS<TeacherResponse> search(com.toan.university_management.dto.request.masterdata.TeacherSearchPaginationRQ search) {
        if (search == null) search = new com.toan.university_management.dto.request.masterdata.TeacherSearchPaginationRQ();
        int page = Math.max(0, search.getPageNumber());
        int size = search.getPageSize() > 0 ? search.getPageSize() : 10;

        String kw = search.getKeyword() != null ? search.getKeyword().trim().toLowerCase() : "";
        String code = search.getTeacherCode() != null ? search.getTeacherCode().trim().toLowerCase() : "";
        String name = search.getFullName() != null ? search.getFullName().trim().toLowerCase() : "";
        String email = search.getEmail() != null ? search.getEmail().trim().toLowerCase() : "";
        String degree = search.getDegree() != null ? search.getDegree().trim().toLowerCase() : "";
        Long deptId = search.getDepartmentId();
        Long provinceId = search.getProvinceId();
        Long districtId = search.getDistrictId();
        Long wardId = search.getWardId();

        List<TeacherResponse> all = getAllTeachers().stream()
                .filter(t -> {
                    if (deptId != null && !Objects.equals(t.getDepartmentId(), deptId)) return false;
                    if (provinceId != null && !Objects.equals(t.getProvinceId(), provinceId)) return false;
                    if (districtId != null && !Objects.equals(t.getDistrictId(), districtId)) return false;
                    if (wardId != null && !Objects.equals(t.getWardId(), wardId)) return false;
                    if (!kw.isEmpty()) {
                        String full = ((t.getTeacherCode() != null ? t.getTeacherCode() : "") + " "
                                + (t.getFullName() != null ? t.getFullName() : "") + " "
                                + (t.getEmail() != null ? t.getEmail() : "") + " "
                                + (t.getDegree() != null ? t.getDegree() : "") + " "
                                + (t.getDepartmentName() != null ? t.getDepartmentName() : "") + " "
                                + (t.getFullAddress() != null ? t.getFullAddress() : "")).toLowerCase();
                        if (!full.contains(kw)) return false;
                    }
                    if (!code.isEmpty() && (t.getTeacherCode() == null || !t.getTeacherCode().toLowerCase().contains(code))) return false;
                    if (!name.isEmpty() && (t.getFullName() == null || !t.getFullName().toLowerCase().contains(name))) return false;
                    if (!email.isEmpty() && (t.getEmail() == null || !t.getEmail().toLowerCase().contains(email))) return false;
                    if (!degree.isEmpty() && (t.getDegree() == null || !t.getDegree().toLowerCase().contains(degree))) return false;
                    return true;
                })
                .toList();
        return com.toan.university_management.common.util.PaginationUtils.paginateList(all, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> export(com.toan.university_management.dto.request.masterdata.TeacherSearchPaginationRQ search) {
        com.toan.university_management.dto.request.masterdata.TeacherSearchPaginationRQ copy = search != null
                ? com.toan.university_management.dto.request.masterdata.TeacherSearchPaginationRQ.builder()
                .keyword(search.getKeyword())
                .teacherCode(search.getTeacherCode())
                .fullName(search.getFullName())
                .email(search.getEmail())
                .phoneNumber(search.getPhoneNumber())
                .degree(search.getDegree())
                .departmentId(search.getDepartmentId())
                .provinceId(search.getProvinceId())
                .districtId(search.getDistrictId())
                .wardId(search.getWardId())
                .pageNumber(0)
                .pageSize(Integer.MAX_VALUE)
                .build()
                : com.toan.university_management.dto.request.masterdata.TeacherSearchPaginationRQ.builder()
                .pageNumber(0)
                .pageSize(Integer.MAX_VALUE)
                .build();
        return search(copy).getItems();
    }

    private TeacherResponse enrichResponse(Teacher teacher) {
        TeacherResponse response = teacherMapper.toTeacherResponse(teacher);
        if (teacher.getDepartmentId() != null) {
            departmentRepository.findByIdAndDeletedFalse(teacher.getDepartmentId()).ifPresent(d -> {
                response.setDepartmentName(d.getName());
            });
        }
        if (teacher.getProvinceId() != null) {
            provinceRepository.findByIdAndDeletedFalse(teacher.getProvinceId()).ifPresent(p -> {
                response.setProvinceName(p.getProvinceName());
            });
        }
        if (teacher.getDistrictId() != null) {
            districtRepository.findByIdAndDeletedFalse(teacher.getDistrictId()).ifPresent(d -> {
                response.setDistrictName(d.getDistrictName());
            });
        }
        if (teacher.getWardId() != null) {
            wardRepository.findByIdAndDeletedFalse(teacher.getWardId()).ifPresent(w -> {
                response.setWardName(w.getWardName());
            });
        }
        if ((response.getSpecificAddress() == null || response.getSpecificAddress().isBlank()) && teacher.getAddress() != null && !teacher.getAddress().isBlank()) {
            response.setSpecificAddress(teacher.getAddress());
        }
        response.setFullAddress(com.toan.university_management.common.util.AddressUtils.buildFullAddress(
                response.getSpecificAddress(), response.getWardName(), response.getDistrictName(), response.getProvinceName(), response.getAddress()));
        return response;
    }

    private List<TeacherResponse> enrichResponses(List<Teacher> teachers) {
        if (teachers.isEmpty()) return Collections.emptyList();

        Set<Long> deptIds = teachers.stream()
                .map(Teacher::getDepartmentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> provinceIds = teachers.stream()
                .map(Teacher::getProvinceId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> districtIds = teachers.stream()
                .map(Teacher::getDistrictId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Long> wardIds = teachers.stream()
                .map(Teacher::getWardId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, Department> deptMap = departmentRepository.findAllByIdInAndDeletedFalse(deptIds)
                .stream().collect(Collectors.toMap(Department::getId, Function.identity()));

        Map<Long, Province> provinceMap = provinceRepository.findAllByIdInAndDeletedFalse(provinceIds)
                .stream().collect(Collectors.toMap(Province::getId, Function.identity()));

        Map<Long, District> districtMap = districtRepository.findAllByIdInAndDeletedFalse(districtIds)
                .stream().collect(Collectors.toMap(District::getId, Function.identity()));

        Map<Long, Ward> wardMap = wardRepository.findAllByIdInAndDeletedFalse(wardIds)
                .stream().collect(Collectors.toMap(Ward::getId, Function.identity()));

        return teachers.stream().map(t -> {
            TeacherResponse res = teacherMapper.toTeacherResponse(t);
            if (t.getDepartmentId() != null && deptMap.containsKey(t.getDepartmentId())) {
                res.setDepartmentName(deptMap.get(t.getDepartmentId()).getName());
            }
            if (t.getProvinceId() != null && provinceMap.containsKey(t.getProvinceId())) {
                res.setProvinceName(provinceMap.get(t.getProvinceId()).getProvinceName());
            }
            if (t.getDistrictId() != null && districtMap.containsKey(t.getDistrictId())) {
                res.setDistrictName(districtMap.get(t.getDistrictId()).getDistrictName());
            }
            if (t.getWardId() != null && wardMap.containsKey(t.getWardId())) {
                res.setWardName(wardMap.get(t.getWardId()).getWardName());
            }
            res.setFullAddress(com.toan.university_management.common.util.AddressUtils.buildFullAddress(
                    res.getSpecificAddress(), res.getWardName(), res.getDistrictName(), res.getProvinceName(), res.getAddress()));
            return res;
        }).toList();
    }
}
