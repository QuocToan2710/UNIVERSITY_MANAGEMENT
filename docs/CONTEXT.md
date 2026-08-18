# CONTEXT.md — Ngữ cảnh hiện tại dự án

> **Loại**: Tài liệu sống — cập nhật cuối mỗi phiên làm việc hoặc khi hoàn thành plan.  
> **Cập nhật lần cuối**: 2026-08-17 17:38

---

## Trạng thái tổng thể: 🟢 Backend & Frontend BUILD SUCCESS 100% — Masterdata Seeded, Timetable Matrix, 4K Glassmorphism Login & Dark/Light Theme System Hoàn tất

## Tính năng đã hoàn thành ✅

| Module              | Trạng thái | Ghi chú                                          |
| ------------------- | ---------- | ------------------------------------------------- |
| Project setup       | ✅ Done     | Spring Boot 3.5.5, Java 21, Maven                |
| MySQL + JPA config  | ✅ Done     | `application.yml`, Hibernate ddl-auto: update, `FOREIGN_KEY_CHECKS=0` |
| Application-Level Relationship | ✅ Done | Loại bỏ 100% DB Foreign Key Constraints (`foreignKey = NO_CONSTRAINT`), quản lý bằng scalar IDs + Index |
| Uniform Identification (`code` & `name`) | ✅ Done | 100% 15 Entities đồng bộ có cặp thuộc tính `code` và `name` |
| Numeric ID Migration | ✅ Done    | 13 Masterdata entities chuyển từ `String UUID` sang `Long` + `GenerationType.IDENTITY` (BIGINT AUTO_INCREMENT) |
| Subject & SubjectClass Refactor | ✅ Done | Chuyển đổi toàn bộ `Course`/`CourseClass` sang `Subject` (Môn học) & `SubjectClass` (Lớp học phần), hỗ trợ backward-compatible JsonAlias |
| Auto DB Cleanup      | ✅ Done    | `cleanDatabase()` trong `UniversityManagementApplication.java`: tự xóa legacy VARCHAR PK tables + orphan tables |
| Security Hardening   | ✅ Done    | Khóa lỗ hổng Unauthenticated Admin Creation (`POST /users`), gỡ bỏ Backdoor mật khẩu Admin cứng |
| Cache Resilience     | ✅ Done    | Thêm `CacheErrorHandler` cho Spring Cache / Redis, tự động fallback DB khi Redis offline |
| FE Form & API Optimization | ✅ Done | Rà soát & sửa API call FE (nhập tay thông tin SV mới, truyền đúng `majorId` & `classGroupId` dạng số khớp DTO) |
| UI Pagination Bar    | ✅ Done    | Component `Pagination` với chọn số dòng/trang, đếm kết quả, nút số trang ở góc dưới bên phải trên tất cả bảng danh sách |
| Pattern Search & Export | ✅ Done | Chuẩn hóa Backend `BasePaginationRS<T>`, `POST /search`, `POST /export` + Component `SearchExportBar` & Xuất Excel `.xlsx` ở FE |
| Redis config        | ✅ Done     | Token blacklist storage                           |
| Entity layer        | ✅ Done     | 19 Entities: 13 Masterdata (`Long IDENTITY`) + 5 Identity (`String UUID`) + 1 Auth (`String manual`) |
| Repository layer    | ✅ Done     | 15+ Repositories với `existsByIdAndDeletedFalse`, `findAllByIdInAndDeletedFalse` |
| Service layer       | ✅ Done     | Interfaces + Implementations với Batch DTO Assembly (chống N+1 Query) |
| Controller layer    | ✅ Done     | 12 REST Controllers với CRUD + Pageable + Search/Export endpoints |
| DTO + MapStruct     | ✅ Done     | Request/Response DTOs, Mappers phẳng hóa          |
| Exception handling  | ✅ Done     | GlobalExceptionHandler, ErrorCode enum, ApiResponse|
| JWT Authentication  | ✅ Done     | Login, logout, refresh token                      |
| Token blacklist     | ✅ Done     | Redis-based, TokenBlacklistFilter                 |
| Dynamic API PBAC    | ✅ Done     | EndpointAutoScanner + DynamicApiAuthorizationManager |
| Public Perms Cache  | ✅ Done     | Spring `@EnableCaching`, `@Cacheable`, `@CacheEvict` |
| Default Roles Init  | ✅ Done     | Tự khởi tạo `ROLE_ADMIN`, `ROLE_USER`, `ROLE_TEACHER`, `ROLE_STUDENT` |
| Admin initializer   | ✅ Done     | Tạo tài khoản Admin/Teacher/Student mặc định      |
| Soft Delete (Xóa mềm)| ✅ Done     | Thêm cột `deleted` và Hibernate `@SQLDelete`, `@SQLRestriction` cho Masterdata |
| Automated Test Suite| ✅ Done     | `./mvnw test` → **BUILD SUCCESS (5/5 tests PASS)** |
| Skills Integration  | ✅ Done     | Cài đặt 9 Agentic Skills từ Pocock set vào `.agents/skills/` |
| React FE Type Sync  | ✅ Done     | Đồng bộ TypeScript types với backend DTOs, `npm run build` PASS 100% |
| Timetable Matrix UI | ✅ Done     | Màn hình Thời khóa biểu dạng Ma trận theo Ca (Ca 1-5, Tiết 1-15) & 7 Ngày (có đầy đủ Chủ nhật), Lọc GV/Phòng, Xuất Excel & In TKB |
| Dark/Light Theme    | ✅ Done     | Hệ thống đổi giao diện Sáng / Tối / Tự động (System) với nút chuyển nhanh trên Header & Profile Dropdown, tối ưu độ tương phản văn bản High-Contrast cho chế độ Sáng |
| 4K Glassmorphism Login | ✅ Done | Màn hình Login ảnh thật 4K Ultra-HD nhìn xuyên thấu (Crystal Clear Glass), tiêu đề khổng lồ có animation dải màu chuyển động từ Trái sang Phải, miễn nhiễm hoàn toàn mảng trắng khi đổi Light Mode |

---

## Cấu trúc DB (19 bảng)

```
Masterdata (13 bảng — Long IDENTITY):
├── building, floor, room
├── department, major, subject
├── teacher, student
├── class_group, subject_class, class_schedule
├── exam_schedule, enrollment

Identity (5 bảng — String UUID):
├── user, role, permission
├── user_role, role_permission

Auth (1 bảng — String manual):
└── invalidated_token
```

---

## Phiên làm việc gần nhất

### 2026-08-17 (Phiên Hoàn thiện Giao diện Đăng nhập 4K Glassmorphism, Theme Sáng/Tối & Masterdata Seeding)

1. **Khởi tạo Masterdata cho toàn bộ 13 Entities (`AdminInitializer.java`)**:
   - Tự động kiểm tra `count == 0` và seed dữ liệu chuẩn mẫu cho 100% các đối tượng chưa có bản ghi:
     - `Subject`: 6 môn học (Java, Web, CSDL, CNPM, Mạng máy tính, Quản trị học).
     - `Department`: 3 khoa (CNTT, DTVT, Kinh tế & Quản trị).
     - `Major`: 5 ngành (Kỹ thuật phần mềm, Khoa học máy tính, Hệ thống thông tin, Điện tử viễn thông, Quản trị kinh doanh).
     - `Building`, `Floor`, `Room`: 4 tòa nhà (A2, B1, A1, C3), 4 tầng, 4 phòng học/lab/hội trường.
     - `Teacher`: 4 giảng viên mẫu (TS. Nguyễn Văn B, ThS. Trần Thị C, PGS.TS. Lê Hoàng D, TS. Phạm Thanh E).
     - `ClassGroup`: 4 lớp hành chính (KTPM K24A, KHMT K24A, DTVT K24, QTKD K24).
     - `Student`: 5 sinh viên mẫu (SV24001 → SV24005) với đầy đủ thông tin ngày sinh, lớp, ngành, tài khoản.
     - `SubjectClass`: 3 lớp học phần (SC_JAVA01, SC_WEB01, SC_DB01).
     - `ClassSchedule`: 3 lịch học theo tuần (Thứ 2, Thứ 4, Thứ 6) gắn phòng học và giảng viên phụ trách.
     - `ExamSchedule`: 3 lịch thi kết thúc học phần & giữa kỳ.
     - `Enrollment`: 4 bản ghi đăng ký học phần kèm điểm số quá trình và kết quả.
   - `./mvnw test` → **BUILD SUCCESS (5/5 tests PASS)**.

2. **Thiết kế Màn hình Thời khóa biểu Ma trận 7 Ngày (`/schedule/timetable`)**:
   - 3 chế độ xem: **Ma trận Ca học (Matrix Grid)**, **Cột 7 Ngày (7-Day Columns)**, **Dạng Bảng (Detailed List)**.
   - Bao quát 5 Ca học (Ca 1-5, Tiết 1-15, 07:00 - 20:15) và **7 ngày trong tuần bao gồm Chủ nhật**.
   - Tích hợp bộ lọc Giảng viên, Phòng học, Học kỳ, Năm học, Thẻ thống kê, Modal Chi tiết, Xuất Excel `.xlsx` và In TKB.

3. **Hệ thống Theme Sáng / Tối (Dark / Light Mode) & High-Contrast Typography**:
   - Quản lý theme qua `ThemeProvider` (`light`, `dark`, `system`) lưu `localStorage`, anti-flicker script trên `root.tsx`.
   - Nút chuyển nhanh Sun ☀️ / Moon 🌙 trên Header và 3 nút tùy chọn trong Profile Dropdown.
   - Tối ưu tương phản chữ chế độ Sáng (`app.css`): Headings đen than `#0f172a`, mô tả xám đậm `#334155`, thẻ và bảng nền rõ ràng, sắc nét.

4. **Nâng cấp Màn hình Đăng nhập 4K Ultra-HD Glassmorphism**:
   - **Ảnh nền 4K**: Kéo ảnh thật kiến trúc trường đại học 4K Ultra-HD (`public/images/university-campus-bg.jpg`, độ phân giải 2560px) hiển thị 100% sống động.
   - **Kính trong suốt nhìn xuyên thấu**: Khung form đăng nhập và 3 khối hộp `01, 02, 03` sử dụng kính pha lê trong suốt (`bg-black/20 backdrop-blur-[3px] border border-white/35`), nhìn thấy trọn vẹn từng vòm cổng và kiến trúc trường phía sau.
   - **Tiêu đề Hero khổng lồ & Animation Gradient Trái → Phải**: Dòng chữ "Quản trị đào tạo / Trực quan & Hiện đại." đạt kích thước lớn (`text-5xl sm:text-6xl lg:text-[4.25rem] font-black`) với hiệu ứng dải màu chuyển động lướt mượt mà liên tục từ trái sang phải (`@keyframes gradientSweepLtr`).
   - **Miễn nhiễm hoàn toàn mảng trắng**: Tự động khóa Dark Mode ở cấp Router khi vào `/login`, ngăn ngừa 100% tình trạng trắng góc khi đăng xuất từ Chế độ Sáng.
   - **Chữ sắc nét từng pixel**: Gỡ bỏ toàn bộ `drop-shadow` tán xạ gây mờ, tối ưu chữ vector đanh thép, rõ ràng và dễ đọc nhất.

5. **Xác thực Kiểm thử & Build Bundle**:
   - Backend: `./mvnw test` → **BUILD SUCCESS 100%**.
   - Frontend: `npm run typecheck` → **0 errors**, `npm run build` → **BUILD SUCCESS 100% (45 modules)**.
