# CONTEXT.md — Ngữ cảnh hiện tại dự án

> **Loại**: Tài liệu sống — cập nhật cuối mỗi phiên làm việc hoặc khi hoàn thành plan.
> **Cập nhật lần cuối**: 2026-08-07

---

## Trạng thái tổng thể: 🟢 Hoạt động tốt & Phân quyền hoàn thiện (~95%)

## Tính năng đã hoàn thành ✅

| Module              | Trạng thái | Ghi chú                                          |
| ------------------- | ---------- | ------------------------------------------------- |
| Project setup       | ✅ Done     | Spring Boot 3.5.5, Java 21, Maven                |
| MySQL + JPA config  | ✅ Done     | `application.yml`, Hibernate ddl-auto: update     |
| Redis config        | ✅ Done     | Token blacklist storage                           |
| Entity layer        | ✅ Done     | User, Role, Permission, Student, Teacher, Course  |
| Repository layer    | ✅ Done     | CRUD + Custom query (CourseRepositoryCustom)       |
| Service layer       | ✅ Done     | Interfaces + implementations cho tất cả entities  |
| Controller layer    | ✅ Done     | 7 REST controllers với CRUD endpoints             |
| DTO + MapStruct     | ✅ Done     | Request/Response DTOs, mapper interfaces          |
| Exception handling  | ✅ Done     | GlobalExceptionHandler, ErrorCode enum, ApiResponse|
| JWT Authentication  | ✅ Done     | Login, logout, refresh token                      |
| Token blacklist     | ✅ Done     | Redis-based, TokenBlacklistFilter                 |
| Dynamic API PBAC    | ✅ Done     | EndpointAutoScanner + DynamicApiAuthorizationManager |
| Public Perms Cache  | ✅ Done     | Spring `@EnableCaching`, `@Cacheable`, `@CacheEvict` |
| Default Roles Init  | ✅ Done     | Tự khởi tạo `ADMIN`, `USER`, `TEACHER`, `STUDENT`  |
| Admin initializer   | ✅ Done     | Tạo tài khoản Admin mặc định khi khởi chạy       |
| CORS configuration  | ✅ Done     | Cho phép React/Vue dev servers                    |
| Management APIs     | ✅ Done     | `PUT /users/{id}/roles`, `PUT /roles/{name}/permissions` |
| Soft Delete (Xóa mềm)| ✅ Done     | Thêm cột `deleted` và Hibernate `@SQLDelete`, `@SQLRestriction` cho Masterdata (Student, Teacher, Course, User) |

## Tính năng đang triển khai 🟡

| Module                          | Trạng thái    | Ghi chú                                                              |
| ------------------------------- | ------------- | --------------------------------------------------------------------- |
| JasperReports integration       | 🟡 Đang làm   | Templates có trong `resources/reports/`, mock data trong `reportmock/`. Chưa rõ hoàn chỉnh chưa. |

## Tính năng chưa làm / Cần bổ sung 🔴

| Hạng mục               | Ưu tiên | Ghi chú                                               |
| ----------------------- | ------- | ------------------------------------------------------ |
| Unit tests              | 🟡 Trung bình | Đã bổ sung `SoftDeleteTest` (4 test cases), cần viết thêm service/controller test |
| Integration tests       | 🔴 Cao  | Testcontainers đã có dependency nhưng chưa viết test   |
| Dockerfile / Docker Compose | 🟡 Trung bình | Chưa có containerization                        |
| CI/CD pipeline          | 🟡 Trung bình | Chưa có GitHub Actions / Jenkins                |
| API documentation       | 🟡 Trung bình | Chưa có Swagger/OpenAPI                         |
| Pagination              | 🟡 Trung bình | Cần xác nhận đã implement hay chưa              |
| Frontend                | 🔵 Thấp | Có folder `react_tutorial/` nhưng chưa rõ liên kết    |

## Issues / Bugs đã xử lý 🐛

| # | Mô tả | Mức độ | Ngày xử lý | Trạng thái |
|---|--------|--------|----------------|------------|
| 1 | Typo tên file `PermissonService` & `PermissionSeviceImpl` | 🟡 Vừa | 2026-08-07 | ✅ Đã fix |
| 2 | `PermissionResponse` thiếu các trường `method`, `endpoint`, `module`, `isPublic` | 🟡 Vừa | 2026-08-07 | ✅ Đã fix |
| 3 | Thiếu `@PermissionMeta(isPublic = true)` trên các Auth endpoints | 🔴 Cao | 2026-08-07 | ✅ Đã fix |
| 4 | Duplicated check token blacklist trong `CustomJwtDecoder` | 🔵 Thấp | 2026-08-07 | ✅ Đã fix |
| 5 | Khởi chạy chưa có sẵn các roles mặc định `USER`, `TEACHER`, `STUDENT` | 🟡 Vừa | 2026-08-07 | ✅ Đã fix |
| 6 | Truy vấn DB liên tục cho Public Permissions ở mỗi Request | 🔴 Cao | 2026-08-07 | ✅ Đã fix (Cacheable) |
| 7 | Thiếu API gán Role cho User & cập nhật Permission cho Role | 🔴 Cao | 2026-08-07 | ✅ Đã fix |
| 8 | Hệ thống xóa cứng (Hard delete) dữ liệu Masterdata | 🔴 Cao | 2026-08-10 | ✅ Đã chuyển sang Soft Delete |

## Phiên làm việc gần nhất

### 2026-08-10
- **Triển khai 2 Issue ưu tiên CAO**:
  1. **Phân trang (Pagination)**: Thêm hỗ trợ `Pageable` cho 4 REST Controllers (`StudentController`, `TeacherController`, `CourseController`, `UserController`) và 4 Services (`StudentService`, `TeacherService`, `CourseService`, `UserService`), mặc định `page = 0, size = 10, sort = "id"`.
  2. **Tối ưu N+1 Query**: Thêm annotation `@EntityGraph` trong `StudentRepository`, `TeacherRepository`, `CourseRepository`, `UserRepository` để tự động eager fetch đối tượng liên quan (`courses`, `teacher`, `roles`) trong 1 truy vấn duy nhất.
- **Sửa lỗi Mức độ CAO (1.1, 1.2, 1.3)** và bổ sung `@Transactional` cho các Services:
  1. **Thread-Safety**: Khởi tạo `NimbusJwtDecoder` trong `@PostConstruct` của `CustomJwtDecoder.java` để đảm bảo an toàn đa luồng.
  2. **Validation**: Bổ sung `@Valid @RequestBody` cho tất cả các endpoints trong `CourseController`, `TeacherController`, `RoleController`, `PermissionController`.
  3. **Security Fix**: Loại bỏ hoàn toàn kiểm tra so sánh mật khẩu dạng plaintext (`equals`) trong `AuthenticationServiceImpl.java`.
  4. **Transactions & Clean Code**: Bổ sung `@Transactional` cho `TeacherServiceImpl`, `CourseServiceImpl`, `UserServiceImpl`, `RoleServiceImpl`, `PermissionServiceImpl` và loại bỏ lệnh `csrf()` trùng lặp trong `SecurityConfig`.
- Chuyển đổi cơ chế xóa từ **Xóa cứng (Hard Delete)** sang **Xóa mềm (Soft Delete)** cho toàn bộ Masterdata nghiệp vụ: `Student`, `Teacher`, `Course`, `User`.
- Bổ sung khai báo tường minh `@Column(name = "...")` cho tất cả các thuộc tính cũ trong 7 Entities (`Student`, `Teacher`, `Course`, `User`, `Role`, `Permission`, `InvalidatedToken`).
- **Triển khai Nhóm 1 (HTTP Error Handlers & Chuẩn hóa Response)**:
  1. **Bổ sung HTTP Exception Handlers**: Thêm `@ExceptionHandler` xử lý `HttpMessageNotReadableException` (Code 1011 - Bad Request), `MethodArgumentTypeMismatchException` (Code 1012 - Bad Request), `HttpRequestMethodNotSupportedException` (Code 1013 - Method Not Allowed) trong [`GlobalExceptionHandler.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/exception/GlobalExceptionHandler.java).
  2. **Chuẩn hóa Thông điệp Response & Typo**: Sửa lỗi chính tả trong `TeacherController.java` (`"Teacher has been delete"` -> `"Teacher has been deleted successfully"`) và thống nhất câu thông báo phản hồi khi xóa trong `StudentController.java`, `CourseController.java`, `UserController.java`.
- **Khởi tạo 3 Tài khoản Mẫu tương ứng 3 Roles**:
  - Cập nhật [`AdminInitializer.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/configuration/AdminInitializer.java) tự động tạo 3 tài khoản khởi tạo khi khởi động server:
    1. **Admin**: `admin` / `admin` (Role `ADMIN`)
    2. **Giảng viên**: `teacher` / `teacher123` (Role `TEACHER`)
    3. **Sinh viên**: `student` / `student123` (Role `STUDENT`)
  - Cập nhật màn hình Đăng nhập Frontend ([`login.tsx`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/react_tutorial/app/routes/login.tsx)) bổ sung 3 nút thử nghiệm nhanh (Quick Demo Login) giúp chuyển đổi linh hoạt giữa 3 vai trò để test giao diện phân quyền.

### 2026-08-07
- Tạo các file ngữ cảnh dự án: `AGENTS.md`, `docs/ARCHITECTURE.md`, `docs/CONTEXT.md`, `README.md`.
- Review hệ thống phân quyền (Authentication & Dynamic Authorization).
- Khắc phục 7 vấn đề liên quan đến Phân quyền & Refactoring code.
- Thêm Spring Cache (`@Cacheable` & `@CacheEvict`) tối ưu hóa hiệu năng kiểm tra API Public Permissions.
- Thêm API `PUT /users/{userId}/roles` (gán vai trò cho người dùng) và `PUT /roles/{roleName}/permissions` (cập nhật quyền cho vai trò).
- Kiểm tra dữ liệu thực tế trong MySQL database (`admin`, `toan01`, `toan04`).
- Biên dịch lại dự án thành công (0 warning/error).

---

## Hướng dẫn cập nhật file này

> [!IMPORTANT]
> **Cuối mỗi phiên làm việc**, hãy yêu cầu AI cập nhật file này với:
> 1. Ghi lại những gì đã làm trong phiên (mục "Phiên làm việc gần nhất")
> 2. Cập nhật trạng thái tính năng nếu có thay đổi
> 3. Thêm issues/bugs mới phát hiện
> 4. Di chuyển tính năng hoàn thành từ 🟡 sang ✅

---

## Chưa khớp thực tế

| Claim | Ý định | Trạng thái | Bằng chứng |
| ----- | ------ | ---------- | ----------- |
| (rỗng — cập nhật khi phát hiện sai lệch) | | | |
