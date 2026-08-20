package com.toan.university_management.configuration;

import com.toan.university_management.entity.identity.Permission;
import com.toan.university_management.entity.identity.Role;
import com.toan.university_management.entity.identity.RolePermission;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.identity.UserRole;
import com.toan.university_management.entity.masterdata.*;
import com.toan.university_management.enums.EnrollmentStatus;
import com.toan.university_management.enums.StudentStatus;
import com.toan.university_management.enums.WeekDay;
import com.toan.university_management.repository.identity.PermissionRepository;
import com.toan.university_management.repository.identity.RolePermissionRepository;
import com.toan.university_management.repository.identity.RoleRepository;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.identity.UserRoleRepository;
import com.toan.university_management.repository.masterdata.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final TeacherRepository teacherRepository;
    private final ClassGroupRepository classGroupRepository;
    private final StudentRepository studentRepository;
    private final SubjectClassRepository subjectClassRepository;
    private final ClassScheduleRepository classScheduleRepository;
    private final EnrollmentRepository enrollmentRepository;
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
        rolePermissionRepository.deleteByRoleId(adminRole.getId());
        rolePermissionRepository.deleteByRoleId(teacherRole.getId());
        rolePermissionRepository.deleteByRoleId(studentRole.getId());
        rolePermissionRepository.deleteByRoleId(userRole.getId());

        for (Permission perm : allPermissions) {
            String m = perm.getMethod() != null ? perm.getMethod().toUpperCase() : "";
            String ep = perm.getEndpoint() != null ? perm.getEndpoint() : "";
            boolean isReadOnly = "GET".equals(m) || ep.endsWith("/search") || ep.endsWith("/combo") || ep.endsWith("/export") || ep.endsWith("/all") || ep.contains("myInfo");

            // Admin: ALL
            rolePermissionRepository.save(RolePermission.builder()
                    .roleId(adminRole.getId())
                    .permissionId(perm.getId())
                    .build());

            // Teacher: Read-only + schedules & exam-schedules & enrollments
            if (isReadOnly || ep.startsWith("/schedules") || ep.startsWith("/exam-schedules") || ep.startsWith("/enrollments")) {
                rolePermissionRepository.save(RolePermission.builder()
                        .roleId(teacherRole.getId())
                        .permissionId(perm.getId())
                        .build());
            }

            // Student: Read-only + enrollments
            if (isReadOnly || ep.startsWith("/enrollments")) {
                rolePermissionRepository.save(RolePermission.builder()
                        .roleId(studentRole.getId())
                        .permissionId(perm.getId())
                        .build());
            }

            // User: Read-only
            if (isReadOnly) {
                rolePermissionRepository.save(RolePermission.builder()
                        .roleId(userRole.getId())
                        .permissionId(perm.getId())
                        .build());
            }
        }

        // Initialize / Refresh Default Users
        User adminUser = initUser("admin", "USR_ADMIN", "admin", "admin@university.edu.vn", "Quản Trị Viên Hệ Thống", adminRole, passwordEncoder);
        User teacherUser = initUser("teacher", "USR_TEACHER", "teacher123", "teacher@university.edu.vn", "Giảng Viên Nguyễn Văn B", teacherRole, passwordEncoder);
        User studentUser = initUser("student", "USR_STUDENT", "student123", "student@university.edu.vn", "Sinh Viên Trần Thị C", studentRole, passwordEncoder);

        // 1. Seed sample Subjects if empty
        if (subjectRepository.count() == 0) {
            subjectRepository.save(Subject.builder().subjectCode("CS101").name("Lập trình Java").credit(3).description("Nhập môn Lập trình Java & OOP").build());
            subjectRepository.save(Subject.builder().subjectCode("CS103").name("Lập trình Web").credit(3).description("Phát triển ứng dụng Web Fullstack").build());
            subjectRepository.save(Subject.builder().subjectCode("DB201").name("Cơ sở dữ liệu").credit(4).description("Hệ quản trị CSDL quan hệ MySQL").build());
            subjectRepository.save(Subject.builder().subjectCode("SE301").name("Công nghệ Phần mềm").credit(3).description("Quy trình thiết kế & kiểm thử phần mềm").build());
            subjectRepository.save(Subject.builder().subjectCode("NT101").name("Mạng Máy tính").credit(3).description("Kiến trúc mạng TCP/IP & bảo mật").build());
            subjectRepository.save(Subject.builder().subjectCode("BA101").name("Quản trị Học đại cương").credit(3).description("Nguyên lý quản trị doanh nghiệp").build());
            log.info("Seeded sample subjects into subject table.");
        }

        // 2. Seed sample Departments & Majors if empty
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

        // 3. Seed sample Buildings & Floors if empty
        if (buildingRepository.count() == 0) {
            Building bA2 = buildingRepository.save(Building.builder().buildingCode("TOA_A2").name("Tòa nhà A2").totalFloors(10).status("ACTIVE").description("Tòa nhà giảng đường chính").build());
            Building bB1 = buildingRepository.save(Building.builder().buildingCode("TOA_B1").name("Tòa nhà B1").totalFloors(6).status("ACTIVE").description("Tòa nhà thực hành & CNTT").build());
            Building bA1 = buildingRepository.save(Building.builder().buildingCode("TOA_A1").name("Tòa nhà A1").totalFloors(8).status("MAINTENANCE").description("Tòa nhà hiệu bộ & hành chính").build());
            Building bC3 = buildingRepository.save(Building.builder().buildingCode("TOA_C3").name("Tòa nhà C3").totalFloors(5).status("ACTIVE").description("Tòa nhà nghiên cứu & thí nghiệm").build());
            log.info("Seeded sample buildings.");

            if (floorRepository.count() == 0) {
                floorRepository.save(Floor.builder().floorCode("TANG_04_A2").name("Tầng 4 - Tòa A2").buildingId(bA2.getId()).floorNumber(4).status("ACTIVE").description("Khu phòng học lý thuyết").build());
                floorRepository.save(Floor.builder().floorCode("TANG_02_B1").name("Tầng 2 - Tòa B1").buildingId(bB1.getId()).floorNumber(2).status("ACTIVE").description("Khu phòng máy tính Lab").build());
                floorRepository.save(Floor.builder().floorCode("TANG_01_A1").name("Tầng 1 - Tòa A1").buildingId(bA1.getId()).floorNumber(1).status("MAINTENANCE").description("Hội trường lớn").build());
                floorRepository.save(Floor.builder().floorCode("TANG_03_C3").name("Tầng 3 - Tòa C3").buildingId(bC3.getId()).floorNumber(3).status("ACTIVE").description("Khu phòng thí nghiệm chuyên ngành").build());
                log.info("Seeded sample floors.");
            }
        }

        // 4. Seed sample Rooms if empty
        if (roomRepository.count() == 0) {
            roomRepository.save(Room.builder().roomCode("A2-402").name("Phòng học A2-402").building("Tòa A2").capacity(80).roomType("Giảng đường").status("ACTIVE").description("Phòng trang bị máy chiếu, điều hòa").build());
            roomRepository.save(Room.builder().roomCode("B1-201").name("Phòng Máy tính Lab 03").building("Tòa B1").capacity(45).roomType("Phòng máy tính").status("ACTIVE").description("45 máy PC cấu hình cao").build());
            roomRepository.save(Room.builder().roomCode("A1-105").name("Phòng học A1-105").building("Tòa A1").capacity(120).roomType("Hội trường nhỏ").status("MAINTENANCE").description("Đang bảo trì hệ thống âm thanh").build());
            roomRepository.save(Room.builder().roomCode("C3-301").name("Phòng Thí nghiệm Viễn thông").building("Tòa C3").capacity(30).roomType("Phòng thí nghiệm").status("ACTIVE").description("Trang bị thiết bị đo kiểm viễn thông").build());
            log.info("Seeded sample rooms.");
        }

        // 5. Seed sample Teachers if empty
        if (teacherRepository.count() == 0) {
            Department firstDept = departmentRepository.findAll().stream().findFirst().orElse(null);
            Long deptId = firstDept != null ? firstDept.getId() : null;

            teacherRepository.save(Teacher.builder()
                    .teacherCode("GV01")
                    .fullName("TS. Nguyễn Văn B")
                    .degree("Tiến sĩ")
                    .email("teacher@university.edu.vn")
                    .phoneNumber("0901234567")
                    .departmentId(deptId)
                    .userId(teacherUser != null ? teacherUser.getId() : null)
                    .build());

            teacherRepository.save(Teacher.builder()
                    .teacherCode("GV02")
                    .fullName("ThS. Trần Thị C")
                    .degree("Thạc sĩ")
                    .email("tranthic@university.edu.vn")
                    .phoneNumber("0912345678")
                    .departmentId(deptId)
                    .build());

            teacherRepository.save(Teacher.builder()
                    .teacherCode("GV03")
                    .fullName("PGS.TS. Lê Hoàng D")
                    .degree("Phó Giáo sư")
                    .email("lehoangd@university.edu.vn")
                    .phoneNumber("0923456789")
                    .departmentId(deptId)
                    .build());

            teacherRepository.save(Teacher.builder()
                    .teacherCode("GV04")
                    .fullName("TS. Phạm Thanh E")
                    .degree("Tiến sĩ")
                    .email("phamthanhe@university.edu.vn")
                    .phoneNumber("0934567890")
                    .departmentId(deptId)
                    .build());

            log.info("Seeded sample teachers.");
        }

        // 6. Seed sample ClassGroups if empty
        if (classGroupRepository.count() == 0) {
            Major firstMajor = majorRepository.findAll().stream().findFirst().orElse(null);
            Teacher firstTeacher = teacherRepository.findAll().stream().findFirst().orElse(null);
            Long mId = firstMajor != null ? firstMajor.getId() : null;
            Long tId = firstTeacher != null ? firstTeacher.getId() : null;

            classGroupRepository.save(ClassGroup.builder()
                    .classCode("CG_KTPM2024")
                    .className("Lớp Kỹ thuật Phần mềm K24A")
                    .majorId(mId)
                    .academicYear("2024-2025")
                    .homeroomTeacherId(tId)
                    .build());

            classGroupRepository.save(ClassGroup.builder()
                    .classCode("CG_KHMT2024")
                    .className("Lớp Khoa học Máy tính K24A")
                    .majorId(mId)
                    .academicYear("2024-2025")
                    .homeroomTeacherId(tId)
                    .build());

            classGroupRepository.save(ClassGroup.builder()
                    .classCode("CG_DTVT2024")
                    .className("Lớp Điện tử Viễn thông K24")
                    .majorId(mId)
                    .academicYear("2024-2025")
                    .homeroomTeacherId(tId)
                    .build());

            classGroupRepository.save(ClassGroup.builder()
                    .classCode("CG_QTKD2024")
                    .className("Lớp Quản trị Kinh doanh K24")
                    .majorId(mId)
                    .academicYear("2024-2025")
                    .homeroomTeacherId(tId)
                    .build());

            log.info("Seeded sample class groups.");
        }

        // 7. Seed sample Students if empty
        if (studentRepository.count() == 0) {
            ClassGroup firstCg = classGroupRepository.findAll().stream().findFirst().orElse(null);
            Major firstMajor = majorRepository.findAll().stream().findFirst().orElse(null);
            Long cgId = firstCg != null ? firstCg.getId() : null;
            Long mId = firstMajor != null ? firstMajor.getId() : null;

            studentRepository.save(Student.builder()
                    .studentCode("SV24001")
                    .fullName("Nguyễn Văn An")
                    .email("student@university.edu.vn")
                    .phoneNumber("0987654321")
                    .dob(java.sql.Date.valueOf(LocalDate.of(2006, 3, 15)))
                    .gender("Nam")
                    .address("Số 12 Chùa Bộc, Đống Đa, Hà Nội")
                    .enrollmentYear("2024")
                    .status(StudentStatus.ACTIVE)
                    .classGroupId(cgId)
                    .majorId(mId)
                    .userId(studentUser != null ? studentUser.getId() : null)
                    .build());

            studentRepository.save(Student.builder()
                    .studentCode("SV24002")
                    .fullName("Trần Thị Mai")
                    .email("maitt@university.edu.vn")
                    .phoneNumber("0987654322")
                    .dob(java.sql.Date.valueOf(LocalDate.of(2006, 7, 22)))
                    .gender("Nữ")
                    .address("Lạch Tray, Ngô Quyền, Hải Phòng")
                    .enrollmentYear("2024")
                    .status(StudentStatus.ACTIVE)
                    .classGroupId(cgId)
                    .majorId(mId)
                    .build());

            studentRepository.save(Student.builder()
                    .studentCode("SV24003")
                    .fullName("Lê Hoàng Long")
                    .email("longlh@university.edu.vn")
                    .phoneNumber("0987654323")
                    .dob(java.sql.Date.valueOf(LocalDate.of(2006, 11, 5)))
                    .gender("Nam")
                    .address("Hải Châu, Đà Nẵng")
                    .enrollmentYear("2024")
                    .status(StudentStatus.ACTIVE)
                    .classGroupId(cgId)
                    .majorId(mId)
                    .build());

            studentRepository.save(Student.builder()
                    .studentCode("SV24004")
                    .fullName("Phạm Minh Tuấn")
                    .email("tuanpm@university.edu.vn")
                    .phoneNumber("0987654324")
                    .dob(java.sql.Date.valueOf(LocalDate.of(2006, 1, 19)))
                    .gender("Nam")
                    .address("Quận 1, TP. Hồ Chí Minh")
                    .enrollmentYear("2024")
                    .status(StudentStatus.ACTIVE)
                    .classGroupId(cgId)
                    .majorId(mId)
                    .build());

            studentRepository.save(Student.builder()
                    .studentCode("SV24005")
                    .fullName("Vũ Phương Anh")
                    .email("anhvp@university.edu.vn")
                    .phoneNumber("0987654325")
                    .dob(java.sql.Date.valueOf(LocalDate.of(2006, 9, 30)))
                    .gender("Nữ")
                    .address("Ninh Kiều, Cần Thơ")
                    .enrollmentYear("2024")
                    .status(StudentStatus.ACTIVE)
                    .classGroupId(cgId)
                    .majorId(mId)
                    .build());

            log.info("Seeded sample students.");
        }

        // 8. Seed sample SubjectClasses if empty
        if (subjectClassRepository.count() == 0) {
            List<Subject> subjects = subjectRepository.findAll();
            List<Teacher> teachers = teacherRepository.findAll();
            Long subId1 = subjects.size() > 0 ? subjects.get(0).getId() : 1L;
            Long subId2 = subjects.size() > 1 ? subjects.get(1).getId() : subId1;
            Long subId3 = subjects.size() > 2 ? subjects.get(2).getId() : subId1;
            Long tId1 = teachers.size() > 0 ? teachers.get(0).getId() : null;
            Long tId2 = teachers.size() > 1 ? teachers.get(1).getId() : tId1;

            subjectClassRepository.save(SubjectClass.builder()
                    .subjectClassCode("SC_JAVA01")
                    .name("Lớp HP Lập trình Java 01")
                    .subjectId(subId1)
                    .teacherId(tId1)
                    .semester("1")
                    .academicYear("2024-2025")
                    .maxCapacity(60)
                    .build());

            subjectClassRepository.save(SubjectClass.builder()
                    .subjectClassCode("SC_WEB01")
                    .name("Lớp HP Lập trình Web 01")
                    .subjectId(subId2)
                    .teacherId(tId2)
                    .semester("1")
                    .academicYear("2024-2025")
                    .maxCapacity(50)
                    .build());

            subjectClassRepository.save(SubjectClass.builder()
                    .subjectClassCode("SC_DB01")
                    .name("Lớp HP Cơ sở dữ liệu 01")
                    .subjectId(subId3)
                    .teacherId(tId1)
                    .semester("1")
                    .academicYear("2024-2025")
                    .maxCapacity(60)
                    .build());

            log.info("Seeded sample subject classes.");
        }

        // 9. Seed sample ClassSchedules if empty
        if (classScheduleRepository.count() == 0) {
            List<SubjectClass> subjectClasses = subjectClassRepository.findAll();
            List<Teacher> teachers = teacherRepository.findAll();
            Long scId1 = subjectClasses.size() > 0 ? subjectClasses.get(0).getId() : 1L;
            Long scId2 = subjectClasses.size() > 1 ? subjectClasses.get(1).getId() : scId1;
            Long scId3 = subjectClasses.size() > 2 ? subjectClasses.get(2).getId() : scId1;
            Long tId1 = teachers.size() > 0 ? teachers.get(0).getId() : null;
            Long tId2 = teachers.size() > 1 ? teachers.get(1).getId() : tId1;

            classScheduleRepository.save(ClassSchedule.builder()
                    .scheduleCode("SCH_JAVA01_T2")
                    .name("Lịch học Java - Thứ 2")
                    .subjectClassId(scId1)
                    .teacherId(tId1)
                    .dayOfWeek(WeekDay.MONDAY)
                    .startTime(LocalTime.of(7, 30))
                    .endTime(LocalTime.of(11, 30))
                    .room("A2-402")
                    .semester("1")
                    .academicYear("2024-2025")
                    .note("Tiết 1-4 tại Giảng đường A2-402")
                    .build());

            classScheduleRepository.save(ClassSchedule.builder()
                    .scheduleCode("SCH_WEB01_T4")
                    .name("Lịch học Web - Thứ 4")
                    .subjectClassId(scId2)
                    .teacherId(tId2)
                    .dayOfWeek(WeekDay.WEDNESDAY)
                    .startTime(LocalTime.of(13, 30))
                    .endTime(LocalTime.of(17, 30))
                    .room("B1-201")
                    .semester("1")
                    .academicYear("2024-2025")
                    .note("Tiết 7-10 tại Lab 03 máy tính")
                    .build());

            classScheduleRepository.save(ClassSchedule.builder()
                    .scheduleCode("SCH_DB01_T6")
                    .name("Lịch học CSDL - Thứ 6")
                    .subjectClassId(scId3)
                    .teacherId(tId1)
                    .dayOfWeek(WeekDay.FRIDAY)
                    .startTime(LocalTime.of(7, 30))
                    .endTime(LocalTime.of(11, 30))
                    .room("A1-105")
                    .semester("1")
                    .academicYear("2024-2025")
                    .note("Tiết 1-4 tại Hội trường A1-105")
                    .build());

            log.info("Seeded sample class schedules.");
        }

        // 10. Seed sample ExamSchedules if empty
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

        // 11. Seed sample Enrollments if empty
        if (enrollmentRepository.count() == 0) {
            List<Student> students = studentRepository.findAll();
            List<SubjectClass> subjectClasses = subjectClassRepository.findAll();
            if (!students.isEmpty() && !subjectClasses.isEmpty()) {
                Student s1 = students.get(0);
                Student s2 = students.size() > 1 ? students.get(1) : s1;
                SubjectClass sc1 = subjectClasses.get(0);
                SubjectClass sc2 = subjectClasses.size() > 1 ? subjectClasses.get(1) : sc1;

                enrollmentRepository.save(Enrollment.builder()
                        .enrollmentCode("ENR_SV01_JAVA")
                        .name("Đăng ký Lập trình Java - SV24001")
                        .studentId(s1.getId())
                        .subjectClassId(sc1.getId())
                        .midtermScore(8.5)
                        .finalScore(9.0)
                        .totalScore(8.8)
                        .status(EnrollmentStatus.PASSED)
                        .enrolledAt(LocalDateTime.now().minusMonths(2))
                        .build());

                enrollmentRepository.save(Enrollment.builder()
                        .enrollmentCode("ENR_SV02_JAVA")
                        .name("Đăng ký Lập trình Java - SV24002")
                        .studentId(s2.getId())
                        .subjectClassId(sc1.getId())
                        .midtermScore(7.5)
                        .finalScore(8.0)
                        .totalScore(7.8)
                        .status(EnrollmentStatus.PASSED)
                        .enrolledAt(LocalDateTime.now().minusMonths(2))
                        .build());

                enrollmentRepository.save(Enrollment.builder()
                        .enrollmentCode("ENR_SV01_WEB")
                        .name("Đăng ký Lập trình Web - SV24001")
                        .studentId(s1.getId())
                        .subjectClassId(sc2.getId())
                        .midtermScore(9.0)
                        .finalScore(9.5)
                        .totalScore(9.3)
                        .status(EnrollmentStatus.PASSED)
                        .enrolledAt(LocalDateTime.now().minusMonths(2))
                        .build());

                log.info("Seeded sample enrollments.");
            }
        }
    }

    private User initUser(String username, String userCode, String rawPassword, String email, String fullName, Role role, PasswordEncoder passwordEncoder) {
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
            if (user.getPassword() == null || user.getPassword().isBlank()) {
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

        // Ensure user-role link exists in user_role table
        if (!userRoleRepository.existsByUserIdAndRoleId(user.getId(), role.getId())) {
            userRoleRepository.save(UserRole.builder().userId(user.getId()).roleId(role.getId()).build());
            log.info("Linked user: {} with role: {}", username, role.getRoleCode());
        }
        return user;
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
