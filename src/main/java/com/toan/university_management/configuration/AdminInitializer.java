package com.toan.university_management.configuration;

import com.toan.university_management.entity.identity.Permission;
import com.toan.university_management.entity.identity.Role;
import com.toan.university_management.entity.identity.RolePermission;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.identity.UserRole;
import com.toan.university_management.entity.masterdata.Department;
import com.toan.university_management.entity.masterdata.ExamSchedule;
import com.toan.university_management.entity.masterdata.Major;
import com.toan.university_management.entity.masterdata.Subject;
import com.toan.university_management.repository.identity.PermissionRepository;
import com.toan.university_management.repository.identity.RolePermissionRepository;
import com.toan.university_management.repository.identity.RoleRepository;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.identity.UserRoleRepository;
import com.toan.university_management.entity.masterdata.Room;
import com.toan.university_management.repository.masterdata.RoomRepository;
import com.toan.university_management.entity.masterdata.Building;
import com.toan.university_management.entity.masterdata.Floor;
import com.toan.university_management.repository.masterdata.BuildingRepository;
import com.toan.university_management.repository.masterdata.FloorRepository;
import com.toan.university_management.repository.masterdata.DepartmentRepository;
import com.toan.university_management.repository.masterdata.ExamScheduleRepository;
import com.toan.university_management.repository.masterdata.MajorRepository;
import com.toan.university_management.repository.masterdata.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final MajorRepository majorRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final RoomRepository roomRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void initAdmin(PasswordEncoder passwordEncoder) {
        initDefaultRoles();

        List<Permission> allPermissions = permissionRepository.findAll();

        Role adminRole = roleRepository.findByRoleCode("ROLE_ADMIN")
                .or(() -> roleRepository.findByName("ADMIN"))
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleCode("ROLE_ADMIN")
                        .name("ADMIN")
                        .description("Administrator role")
                        .build()));

        Role teacherRole = roleRepository.findByRoleCode("ROLE_TEACHER")
                .or(() -> roleRepository.findByName("TEACHER"))
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleCode("ROLE_TEACHER")
                        .name("TEACHER")
                        .description("Teacher role")
                        .build()));

        Role studentRole = roleRepository.findByRoleCode("ROLE_STUDENT")
                .or(() -> roleRepository.findByName("STUDENT"))
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleCode("ROLE_STUDENT")
                        .name("STUDENT")
                        .description("Student role")
                        .build()));

        Role userRole = roleRepository.findByRoleCode("ROLE_USER")
                .or(() -> roleRepository.findByName("USER"))
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleCode("ROLE_USER")
                        .name("USER")
                        .description("User role")
                        .build()));

        // Sync permissions for all roles
        rolePermissionRepository.deleteByRoleCode(adminRole.getRoleCode());
        rolePermissionRepository.deleteByRoleCode(teacherRole.getRoleCode());
        rolePermissionRepository.deleteByRoleCode(studentRole.getRoleCode());
        rolePermissionRepository.deleteByRoleCode(userRole.getRoleCode());

        for (Permission perm : allPermissions) {
            String m = perm.getMethod() != null ? perm.getMethod().toUpperCase() : "";
            String ep = perm.getEndpoint() != null ? perm.getEndpoint() : "";
            String code = perm.getPermissionCode() != null ? perm.getPermissionCode() : perm.getName();

            // Admin: ALL
            rolePermissionRepository.save(RolePermission.builder()
                    .roleCode(adminRole.getRoleCode())
                    .permissionCode(code)
                    .build());

            // Teacher: GET on all masterdata/schedules/exam-schedules/reports, POST/PUT on schedules & enrollments
            if ("GET".equals(m) || ep.startsWith("/schedules") || ep.startsWith("/exam-schedules") || ep.startsWith("/enrollments")) {
                rolePermissionRepository.save(RolePermission.builder()
                        .roleCode(teacherRole.getRoleCode())
                        .permissionCode(code)
                        .build());
            }

            // Student: GET on masterdata/schedules/exam-schedules, POST/GET on enrollments
            if ("GET".equals(m) || ep.startsWith("/enrollments")) {
                rolePermissionRepository.save(RolePermission.builder()
                        .roleCode(studentRole.getRoleCode())
                        .permissionCode(code)
                        .build());
            }

            // User: GET on myInfo / subjects
            if ("GET".equals(m) && (ep.contains("myInfo") || ep.startsWith("/subjects"))) {
                rolePermissionRepository.save(RolePermission.builder()
                        .roleCode(userRole.getRoleCode())
                        .permissionCode(code)
                        .build());
            }
        }

        // Initialize / Refresh Default Users
        initUser("admin", "USR_ADMIN", "admin", "admin@university.edu.vn", "Quản Trị Viên Hệ Thống", adminRole.getRoleCode(), passwordEncoder);
        initUser("teacher", "USR_TEACHER", "teacher123", "teacher@university.edu.vn", "Giảng Viên Nguyễn Văn B", teacherRole.getRoleCode(), passwordEncoder);
        initUser("student", "USR_STUDENT", "student123", "student@university.edu.vn", "Sinh Viên Trần Thị C", studentRole.getRoleCode(), passwordEncoder);

        // Seed sample subjects if subject table is empty

        // Fallback: Seed sample subjects if subject table is still empty
        if (subjectRepository.count() == 0) {
            subjectRepository.save(Subject.builder().subjectCode("CS101").name("Lập trình Java").credit(3).description("Nhập môn Lập trình Java").build());
            subjectRepository.save(Subject.builder().subjectCode("CS103").name("Lập trình Web").credit(3).description("Phát triển ứng dụng Web").build());
            subjectRepository.save(Subject.builder().subjectCode("DB201").name("Cơ sở dữ liệu").credit(4).description("Hệ quản trị CSDL MySQL").build());
            log.info("Seeded sample subjects into subject table.");
        }

        // Seed sample Departments & Majors if empty
        if (departmentRepository.count() == 0) {
            Department cntt = departmentRepository.save(Department.builder().departmentCode("DEPT_CNTT").name("Khoa Công nghệ Thông tin").description("Đào tạo Công nghệ thông tin & Phần mềm").build());
            Department dtvt = departmentRepository.save(Department.builder().departmentCode("DEPT_DTVT").name("Khoa Điện tử Viễn thông").description("Đào tạo Điện tử & Viễn thông").build());
            Department kt = departmentRepository.save(Department.builder().departmentCode("DEPT_KT").name("Khoa Kinh tế & Quản trị").description("Đào tạo Quản trị & Tài chính").build());
            log.info("Seeded sample departments.");

            if (majorRepository.count() == 0) {
                majorRepository.save(Major.builder().majorCode("MJ_KTPM").name("Kỹ thuật Phần mềm").departmentId(cntt.getId()).build());
                majorRepository.save(Major.builder().majorCode("MJ_KHMT").name("Khoa học Máy tính").departmentId(cntt.getId()).build());
                majorRepository.save(Major.builder().majorCode("MJ_HTTT").name("Hệ thống Thông tin").departmentId(cntt.getId()).build());
                majorRepository.save(Major.builder().majorCode("MJ_DTVT").name("Điện tử Viễn thông").departmentId(dtvt.getId()).build());
                majorRepository.save(Major.builder().majorCode("MJ_QTKD").name("Quản trị Kinh doanh").departmentId(kt.getId()).build());
                log.info("Seeded sample majors.");
            }
        }

        // Seed sample ExamSchedules if empty
        if (examScheduleRepository.count() == 0) {
            examScheduleRepository.save(ExamSchedule.builder()
                    .examCode("EXAM_JAVA01_HK1")
                    .name("Thi kết thúc HP Lập trình Java")
                    .examDate(LocalDate.of(2026, 1, 15))
                    .startTime(LocalTime.of(8, 0))
                    .endTime(LocalTime.of(10, 0))
                    .room("Phòng A2-402")
                    .examFormat("Tự luận")
                    .proctorName("TS. Nguyễn Văn B")
                    .semester("1")
                    .academicYear("2024-2025")
                    .build());

            examScheduleRepository.save(ExamSchedule.builder()
                    .examCode("EXAM_WEB02_HK1")
                    .name("Thi giữa kỳ Lập trình Web")
                    .examDate(LocalDate.of(2026, 1, 18))
                    .startTime(LocalTime.of(13, 30))
                    .endTime(LocalTime.of(15, 0))
                    .room("Lab 3 - B1-201")
                    .examFormat("Thực hành")
                    .proctorName("ThS. Trần Thị C")
                    .semester("1")
                    .academicYear("2024-2025")
                    .build());

            examScheduleRepository.save(ExamSchedule.builder()
                    .examCode("EXAM_DB201_HK1")
                    .name("Thi kết thúc HP Cơ sở dữ liệu")
                    .examDate(LocalDate.of(2026, 1, 22))
                    .startTime(LocalTime.of(9, 30))
                    .endTime(LocalTime.of(11, 0))
                    .room("Phòng A1-105")
                    .examFormat("Trắc nghiệm")
                    .proctorName("TS. Nguyễn Văn B")
                    .semester("1")
                    .academicYear("2024-2025")
                    .build());

            log.info("Seeded sample exam schedules.");
        }

        // Seed sample Rooms if empty
        if (roomRepository.count() == 0) {
            roomRepository.save(Room.builder()
                    .roomCode("A2-402")
                    .name("Phòng học A2-402")
                    .building("Tòa A2")
                    .capacity(80)
                    .roomType("Giảng đường")
                    .status("ACTIVE")
                    .description("Phòng trang bị máy chiếu, điều hòa")
                    .build());

            roomRepository.save(Room.builder()
                    .roomCode("B1-201")
                    .name("Phòng Máy tính Lab 03")
                    .building("Tòa B1")
                    .capacity(45)
                    .roomType("Phòng máy tính")
                    .status("ACTIVE")
                    .description("45 máy PC cấu hình cao")
                    .build());

            roomRepository.save(Room.builder()
                    .roomCode("A1-105")
                    .name("Phòng học A1-105")
                    .building("Tòa A1")
                    .capacity(120)
                    .roomType("Hội trường nhỏ")
                    .status("MAINTENANCE")
                    .description("Đang bảo trì hệ thống âm thanh")
                    .build());

            roomRepository.save(Room.builder()
                    .roomCode("C3-301")
                    .name("Phòng Thí nghiệm Viễn thông")
                    .building("Tòa C3")
                    .capacity(30)
                    .roomType("Phòng thí nghiệm")
                    .status("ACTIVE")
                    .description("Trang bị thiết bị đo kiểm viễn thông")
                    .build());

            log.info("Seeded sample rooms.");
        }

        // Seed sample Buildings & Floors if empty
        if (buildingRepository.count() == 0) {
            Building bA2 = buildingRepository.save(Building.builder().buildingCode("TOA_A2").name("Tòa nhà A2").totalFloors(10).status("ACTIVE").description("Tòa nhà giảng đường chính").build());
            Building bB1 = buildingRepository.save(Building.builder().buildingCode("TOA_B1").name("Tòa nhà B1").totalFloors(6).status("ACTIVE").description("Tòa nhà thực hành & CNTT").build());
            Building bA1 = buildingRepository.save(Building.builder().buildingCode("TOA_A1").name("Tòa nhà A1").totalFloors(8).status("MAINTENANCE").description("Tòa nhà hiệu bộ & hành chính").build());
            log.info("Seeded sample buildings.");

            if (floorRepository.count() == 0) {
                floorRepository.save(Floor.builder().floorCode("TANG_04_A2").name("Tầng 4 - Tòa A2").buildingId(bA2.getId()).floorNumber(4).status("ACTIVE").description("Khu phòng học lý thuyết").build());
                floorRepository.save(Floor.builder().floorCode("TANG_02_B1").name("Tầng 2 - Tòa B1").buildingId(bB1.getId()).floorNumber(2).status("ACTIVE").description("Khu phòng máy tính Lab").build());
                floorRepository.save(Floor.builder().floorCode("TANG_01_A1").name("Tầng 1 - Tòa A1").buildingId(bA1.getId()).floorNumber(1).status("MAINTENANCE").description("Hội trường lớn").build());
                log.info("Seeded sample floors.");
            }
        }
    }

    private void initUser(String username, String userCode, String rawPassword, String email, String fullName, String roleCode, PasswordEncoder passwordEncoder) {
        var existingUser = userRepository.findByUsername(username)
                .or(() -> userRepository.findByUsernameIgnoreCase(username));
        User user;
        if (existingUser.isEmpty()) {
            user = User.builder()
                    .userCode(userCode)
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .email(email)
                    .fullName(fullName)
                    .build();
            user = userRepository.save(user);
            log.info("Initialized default user: {}", username);
        } else {
            user = existingUser.get();
            boolean needSave = false;
            if (user.getUserCode() == null || user.getUserCode().isBlank()) {
                user.setUserCode(userCode);
                needSave = true;
            }
            if (user.getPassword() == null || user.getPassword().isBlank() || !passwordEncoder.matches(rawPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(rawPassword));
                needSave = true;
            }
            if (user.getFullName() == null || user.getFullName().isBlank()) {
                user.setFullName(fullName);
                needSave = true;
            }
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                user.setEmail(email);
                needSave = true;
            }
            if (needSave) {
                user = userRepository.save(user);
                log.info("Updated attributes for user: {}", username);
            }
        }

        // Purge empty role mapping if present
        userRoleRepository.deleteByUserIdAndRoleCode(user.getId(), "");
        userRoleRepository.deleteByUserIdAndRoleCode(user.getId(), null);

        // Ensure user-role link exists in user_role table
        if (!userRoleRepository.existsByUserIdAndRoleCode(user.getId(), roleCode)) {
            userRoleRepository.save(UserRole.builder().userId(user.getId()).roleCode(roleCode).build());
            log.info("Linked user: {} with role: {}", username, roleCode);
        }
    }

    private void initDefaultRoles() {
        Map<String, String> defaultRoles = Map.of(
            "ROLE_USER", "Default user role",
            "ROLE_TEACHER", "Teacher role",
            "ROLE_STUDENT", "Student role",
            "ROLE_ADMIN", "Administrator role"
        );

        for (Map.Entry<String, String> entry : defaultRoles.entrySet()) {
            String roleCode = entry.getKey();
            String roleName = roleCode.replace("ROLE_", "");
            roleRepository.findByRoleCode(roleCode)
                .or(() -> roleRepository.findByName(roleName))
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .roleCode(roleCode)
                            .name(roleName)
                            .description(entry.getValue())
                            .build();
                    log.info("Created default role: {}", roleCode);
                    return roleRepository.save(role);
                });
        }
    }
}
