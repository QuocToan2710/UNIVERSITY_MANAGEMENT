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

## Tính năng đang triển khai 🟡

| Module                          | Trạng thái    | Ghi chú                                                              |
| ------------------------------- | ------------- | --------------------------------------------------------------------- |
| JasperReports integration       | 🟡 Đang làm   | Templates có trong `resources/reports/`, mock data trong `reportmock/`. Chưa rõ hoàn chỉnh chưa. |

## Tính năng chưa làm / Cần bổ sung 🔴

| Hạng mục               | Ưu tiên | Ghi chú                                               |
| ----------------------- | ------- | ------------------------------------------------------ |
| Unit tests              | 🔴 Cao  | Chỉ có file test skeleton, chưa có test thực tế        |
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

## Phiên làm việc gần nhất

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
