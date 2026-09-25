# Context Phiên Làm Việc (Work Session Context)

*Thời gian cập nhật: 25/08/2026*

---

## 1. Tổng quan hệ thống (System Overview)

- **Frontend Techstack:** React 19, React Router v8, Vite, TypeScript, Tailwind CSS v4.
- **Backend Techstack:** Java Spring Boot 3, Spring Security, JWT, Spring Data JPA / Hibernate, MySQL.
- **Thư mục Frontend chính:** `D:\My Project\UNIVERSITY_MANAGEMENT\react_tutorial\`
- **Thư mục Backend chính:** `D:\My Project\UNIVERSITY_MANAGEMENT\university-management\`
- **Kiến trúc phân tầng:**
  - `app/routes/`: 16 màn hình chức năng (Tổng quan, Sinh viên, Giảng viên, Môn học, Ngành học, Lớp học phần, Tài khoản, Thông báo, Danh mục Tòa/Tầng/Phòng/Địa giới, Lịch học/Lịch thi/Lịch dạy/Thời khóa biểu matrix, Đăng nhập).
  - `app/components/`: Khung ứng dụng `app-shell.tsx`, `icons.tsx`, thanh tìm kiếm & lọc `search-export-bar.tsx`, phân trang `pagination.tsx`, ma trận `timetable.tsx`, badge trạng thái `status-badge.tsx`, trạng thái rỗng `empty-state.tsx`, v.v.
  - `app/components/forms/`: 12 Modal Forms tạo mới/chỉnh sửa thực thể.
  - `app/services/`: 13 Domain API services chuẩn hóa (`student.service.ts`, `teacher.service.ts`, `schedule.service.ts`, `notification.service.ts`, v.v.).
  - `app/constants/`: Tập trung hằng số hệ thống `app.constant.ts`, `endpoints.constant.ts`.
  - `app/contexts/`: `theme-context.tsx` quản lý Dark/Light/System theme.
  - `app/app.css`: Design System, CSS Typography & GPU Animation Rules.

---

## 2. Các công việc đã hoàn thành trong phiên làm việc ngày 25/08/2026 (Completed Work)

### A. Quản lý Tài khoản & Gửi Mail Chào mừng Tự động (Email Provisioning & Transactional Consistency)
- **Tự động cấp tài khoản User khi tạo Sinh viên / Giảng viên:**
  - Tạo mới Sinh viên hoặc Giảng viên sẽ tự động tạo một tài khoản `User` với quyền tương ứng (`ROLE_STUDENT` hoặc `ROLE_TEACHER`), mật khẩu mặc định gắn với mã sinh viên/giảng viên.
  - Đồng bộ email cá nhân của sinh viên/giảng viên vào `User.email`.
- **Gửi Email HTML Chào mừng:** Gửi email template HTML đẹp mắt thông báo thông tin tài khoản (Tên đăng nhập, Mật khẩu khởi tạo, Link đăng nhập) đến email của sinh viên/giảng viên.
- **Toàn vẹn Transaction & Rollback:** Bọc `@Transactional(rollbackFor = Exception.class)` cho toàn bộ luồng tạo/cập nhật. Nếu tạo User thất bại hoặc lỗi hệ thống, toàn bộ tiến trình tạo Sinh viên/Giảng viên sẽ tự động rollback 100%.
- **Đồng bộ 2 chiều (Bi-directional Sync):** Khi cập nhật hoặc xóa mềm Sinh viên/Giảng viên, tài khoản `User` liên kết cũng được cập nhật thông tin (`email`, `fullName`) hoặc xóa mềm đồng bộ.
- **Đăng nhập linh hoạt:** Hỗ trợ đăng nhập bằng cả `username` hoặc `email`.
- **Khôi phục mật khẩu OTP:** Xác thực OTP gửi qua email (hỗ trợ Redis kèm In-memory Fallback).

---

### B. Khắc phục Tra cứu Thời khóa biểu & Lịch thi (Timetable & Exam Schedule Multi-Tier Fallback)
- Nâng cấp cơ chế tìm kiếm trong `ClassScheduleServiceImpl.java` và `ExamScheduleServiceImpl.java` lên thuật toán **4-tier fallback**:
  $$\text{userId} \longrightarrow \text{username} \longrightarrow \text{userCode} \longrightarrow \text{email}$$
- Giải quyết triệt để vấn đề sinh viên/giảng viên đăng nhập nhưng không tìm thấy lịch học/lịch thi do lệch ID hoặc mã định danh.

---

### C. Chuẩn hóa Cấu trúc Thư mục `common` & `constant` (Clean Architecture Standardization)
- **Backend (`com.toan.university_management.constant` & `common.util`):**
  - `AppConstants.java`: Tập trung các hằng số phân trang mặc định (`page=0, size=10, maxSize=1000`), thời hạn OTP (10 phút), học kỳ mặc định (`"HK1"`, `"2025-2026"`), định dạng ngày giờ (`"yyyy-MM-dd HH:mm:ss"`).
  - `RoleConstants.java`: Centralized constants cho `ROLE_ADMIN`, `ROLE_TEACHER`, `ROLE_STUDENT`.
  - `MessageConstants.java`: Tập trung các câu thông báo phản hồi chuẩn hóa.
  - `PaginationUtils.java`: Generic helper `paginateList(items, page, size)` tái sử dụng trên toàn bộ các Service.
  - `AddressUtils.java`: Tiện ích ghép nối địa chỉ `buildFullAddress(specific, ward, district, province)`.
  - `SecurityUtils.java` & `DateTimeUtils.java`: Hỗ trợ lấy thông tin user đăng nhập và định dạng thời gian.
- **Frontend (`react_tutorial/app`):**
  - `constants/app.constant.ts`: Hằng số Roles, PageSize, Semester, Storage Keys.
  - `constants/endpoints.constant.ts`: Tập trung các URL endpoint API.
  - `components/status-badge.tsx`: Component badge trạng thái màu sắc chuẩn (`ACTIVE`, `INACTIVE`, `PENDING`, `SUBMITTED`, `PUBLISHED`, `LOCKED`).
  - `components/empty-state.tsx`: Component hiển thị trạng thái dữ liệu trống.
  - `lib/formatters.ts`: Bộ tiện ích format ngày tháng tiếng Việt và điểm số GPA/CPA.

---

### D. Triển khai Tầng Base Entity & JPA Auditing Tự động (Enterprise Audit Trail)
- **`BaseEntity.java` (`common/entity/BaseEntity.java`):**
  - Đã loại bỏ trường `deleted` ra khỏi BaseEntity để đảm bảo tính độc lập và linh hoạt cho từng bảng.
  - Tích hợp 100% tự động các trường kiểm vết (Audit Trail):
    - `id`: Khóa chính `Long` tự tăng.
    - `createdAt`: Ngày giờ tạo bản ghi (`@CreatedDate`).
    - `createdBy`: Username người tạo bản ghi (`@CreatedBy`).
    - `updatedAt`: Ngày giờ sửa cuối (`@LastModifiedDate`).
    - `updatedBy`: Username người sửa cuối (`@LastModifiedBy`).
- **`JpaAuditingConfig.java` (`configuration/JpaAuditingConfig.java`):**
  - Bật `@EnableJpaAuditing`.
  - Triển khai `AuditorAware<String>` tự động lấy username của người đang thao tác từ `SecurityContextHolder` (hoặc `"SYSTEM"` nếu gọi nền/khởi tạo).
- **Phủ sóng 100% Entities:** Áp dụng kế thừa `BaseEntity` (kèm `@SuperBuilder`) cho **23/23 Entity** có khóa chính số (`User`, `Role`, `Permission`, `UserRole`, `RolePermission`, `Student`, `Teacher`, `Department`, `Major`, `Subject`, `SubjectClass`, `ClassGroup`, `ClassSchedule`, `ExamSchedule`, `Enrollment`, `Building`, `Floor`, `Room`, `Province`, `District`, `Ward`, `Notification`, `UserNotification`).
- **`BaseRepository.java` (`common/repository/BaseRepository.java`):**
  - Kế thừa `JpaRepository<T, ID>` với các phương thức xóa mềm chuẩn hóa: `findByIdAndDeletedFalse`, `findAllByDeletedFalse`, `existsByIdAndDeletedFalse`, `findAllByIdInAndDeletedFalse`.
  - Áp dụng trên toàn bộ các Repository trong dự án.
- **`BaseSearchPaginationRQ.java` & `BaseResponse.java` (`common/dto`):** Chuẩn hóa khung DTO phân trang và phản hồi.

---

### E. Tối ưu PasswordEncoder & Chế độ Mật khẩu Chữ thuần (Development Plain-Text Mode)
- **`PasswordEncoderConfig.java`:**
  - Cấu hình bộ mã hóa trả về chuỗi thuần (Plain-text String) khi tạo mới hoặc cập nhật mật khẩu, giúp mật khẩu được lưu trực tiếp dạng text trong Database để dễ dàng kiểm thử và debug.
  - **Cơ chế Dual-Matching (Tương thích ngược):** So khớp trực tiếp chuỗi thuần, đồng thời tự động nhận diện và hỗ trợ giải mã các mật khẩu cũ trong Database đang lưu dạng hash BCrypt (`$2a$`), đảm bảo tất cả các tài khoản (đặc biệt là `admin` / `admin`) luôn đăng nhập thành công 100%.
  - `AdminInitializer.java`: Tự động đồng bộ các tài khoản seed ban đầu (`admin`/`admin`, `teacher`/`teacher123`, `student`/`student123`).

---

## 3. Các công việc đã hoàn thành trong phiên làm việc ngày 26/08/2026 (Completed Work)

### A. Triển khai Phân hệ Tài chính & Quản lý Học phí (Tuition Fee & Finance Module)
- **Backend (`TuitionFee.java`, `TuitionFeeRepository.java`, `TuitionServiceImpl.java`, `TuitionController.java`):**
  - Tạo entity `TuitionFee` (kế thừa `BaseEntity`, tích hợp soft-delete `deleted`/`deletedKey`), enum `TuitionStatus` (`UNPAID`, `PARTIALLY_PAID`, `PAID`, `OVERDUE`).
  - Tự động tính toán số tiền học phí và cập nhật trạng thái nộp tiền (`calculateAmounts()`: $totalAmount = totalCredits \times pricePerCredit$).
  - API `GET /tuition/my`: Lấy phiếu báo học phí cá nhân của sinh viên theo học kỳ & năm học kèm bảng kê chi tiết từng lớp học phần đã đăng ký.
  - API `GET /tuition/all`: Quản lý danh sách công nợ học phí toàn trường cho Admin/Kế toán, hỗ trợ phân trang, lọc theo học kỳ, năm học, lớp sinh hoạt, trạng thái nộp tiền và tìm kiếm sinh viên.
  - API `GET /tuition/dashboard`: Tổng hợp số liệu KPI tài chính (Tổng học phí phát sinh, Tổng thực thu, Tổng công nợ, Tỷ lệ thu hồi học phí %).
  - API `POST /tuition/payment`: Ghi nhận thu học phí trực tiếp (Chuyển khoản, Tiền mặt, POS), cập nhật công nợ và tự động gửi thông báo đến sinh viên.
  - Khắc phục triệt để lỗi khởi động Spring Boot do thiếu thuộc tính soft-delete trong `TuitionFee`.

- **Frontend (`app/routes/finance/tuition.tsx`, `app/services/tuition.service.ts`):**
  - Tạo mới màn hình Học phí (`/finance/tuition`) với phân quyền giao diện chặt chẽ:
    - **Sinh viên:** Thẻ thông tin cá nhân, 3 thẻ KPI tài chính (Học phí phát sinh, Đã thanh toán, Còn nợ & Hạn nộp), Bảng chi tiết từng lớp học phần phát sinh học phí, xuất phiếu báo Excel.
    - **Admin / Kế toán:** Dashboard 4 KPI tài chính, thanh bộ lọc học kỳ, năm học, lớp sinh hoạt, trạng thái nộp, ô tìm kiếm, bảng công nợ học phí toàn trường, Modal ghi nhận thu tiền học phí, Modal xem bảng kê học phí chi tiết từng môn của sinh viên, xuất báo cáo công nợ Excel.
  - Tích hợp menu **Tài chính > Học phí** (`BanknotesIcon`) trên thanh điều hướng sidebar.
  - Cột thao tác chuẩn hóa dropdown 3 chấm (`ActionDropdown`) và sắp xếp kích thước cột vừa vặn, responsive.

---

### B. Chuẩn hóa Phân quyền Thanh Điều hướng theo Role (Role-based Navigation Access Control)
- **Màn hình Ngành học (`/majors`):** Giới hạn quyền chỉ dành riêng cho `ADMIN` (`allowedRoles: ["ADMIN"]`), ẩn hoàn toàn khỏi Sinh viên và Giảng viên.
- **Màn hình Môn học (`/courses`):** Giới hạn quyền dành cho `ADMIN` và `TEACHER`, ẩn khỏi Sinh viên.
- **Nhóm Lịch (`/schedule`):** Sinh viên chỉ nhìn thấy duy nhất menu **"Thời khóa biểu"** (`/schedule/timetable`), ẩn các màn hình quản trị lịch dạy, lịch học và lịch thi.

---

### C. Nâng cấp Toàn diện Màn hình Lịch Giảng dạy (Teaching Schedule Full CRUD & Action Menu)
- **Giao diện (`app/routes/schedules/teaching.tsx`):**
  - Tích hợp đầy đủ các chức năng **Thêm mới / Phân công lịch dạy** (`ScheduleForm`), **Xem chi tiết**, **Chỉnh sửa** và **Xóa** (`ConfirmModal`) cho Admin.
  - Thống kê 3 Widget KPI: Tổng số ca dạy, Định mức tiết giảng dạy/tuần, Số phòng học sử dụng.
  - Bộ lọc Học kỳ, Năm học, ô tìm kiếm thời gian thực (Mã TKB, Lớp học phần, Tên giảng viên, Phòng học) và nút xuất Excel.
  - Cột Thao tác sử dụng dropdown ba chấm `...` (`ActionDropdown`) đồng bộ với toàn hệ thống.

---

### D. Xây dựng Cụm Chức năng "Quản trị hệ thống" & Màn hình Quản lý Vai trò (Role Management)
- **Menu Quản trị hệ thống (`app-shell.tsx`):**
  - Gom nhóm chức năng hệ thống với icon bánh răng `CogIcon` (chỉ dành cho `ADMIN`), gồm 2 menu con:
    - **Người dùng** (`/users`): Quản lý tài khoản, gán vai trò người dùng.
    - **Vai trò** (`/roles`): Quản lý Roles (`ADMIN`, `TEACHER`, `STUDENT`, `STAFF`, v.v.) và ma trận phân quyền API chi tiết.
- **Màn hình Quản lý Vai trò & Phân quyền (`app/routes/roles.tsx`):**
  - **KPI Header:** Tổng số vai trò, Tổng số quyền hạn hệ thống (API Permissions), Số vai trò mặc định.
  - **Danh sách vai trò:** Hiển thị Mã vai trò, Tên vai trò, Mô tả, Badge số lượng quyền được gán, Phân loại (Hệ thống / Tùy chỉnh).
  - **Thêm mới vai trò & Phân quyền:** Form nhập thông tin và ma trận chọn quyền theo từng phân hệ (Identity, Schedule, Grade, Tuition, Masterdata, Notification, v.v.), hỗ trợ tìm kiếm quyền và chọn nhanh toàn bộ quyền của phân hệ.
  - **Xem chi tiết quyền hạn:** Modal bảng kê chi tiết toàn bộ Endpoint, Method và mô tả quyền hạn của vai trò.
  - **Xóa vai trò:** Modal xác nhận xóa an toàn (`ConfirmModal`) cho các vai trò tùy chỉnh.
  - **Xuất Excel:** Kết xuất danh sách vai trò hệ thống ra file Excel.
- Đăng ký route `/roles` trong `routes.ts`.

---

---

## 4. Các công việc đã hoàn thành trong phiên làm việc ngày 27/08/2026 (Completed Work)

### A. Triển khai Phân hệ Điểm danh & Cảnh báo Chuyên cần (Attendance & Absence Warning Module)
- **Tài liệu đặc tả nghiệp vụ:** Đã ban hành tài liệu đặc tả chuẩn hóa tại [`university-management/docs/ATTENDANCE_MODULE_SPEC.md`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/docs/ATTENDANCE_MODULE_SPEC.md).
- **Backend Core & Database Schema:**
  - Tạo các Enums: `AttendanceStatus` (`PRESENT`, `LATE`, `EXCUSED`, `UNEXCUSED`), `AttendanceSessionStatus` (`PENDING`, `COMPLETED`), thêm `ATTENDANCE` vào `NotificationType`.
  - Tạo Entity `AttendanceSession` và `AttendanceRecord` kế thừa `BaseEntity` với soft-delete và JPA auditing.
  - Cập nhật `Enrollment` bổ sung các trường: `totalSessions`, `absentSessions`, `absenceRate`, `isBannedFromExam`.
  - Tạo Repositories `AttendanceSessionRepository`, `AttendanceRecordRepository`, mở rộng `EnrollmentRepository` và `ClassScheduleRepository`.
  - Tạo DTOs (AutoGenerate, Session, RecordItem, Submit, StudentSummary, BannedStudent) và MapStruct mappers `AttendanceSessionMapper`, `AttendanceRecordMapper`.
- **Business Logic & Auto-Warning Engine (`AttendanceServiceImpl.java`):**
  - Tự động sinh danh sách $N$ buổi học cách nhau 1 tuần dựa trên Thời khóa biểu (`autoGenerateSessions`) hoặc thêm buổi học bù.
  - Xử lý điểm danh nhanh (1-click Có mặt, đánh dấu Đi muộn theo phút, Vắng có phép/không phép, nhập lý do).
  - Tự động tính toán điểm chuyên cần thang điểm 10:
    $$\text{Điểm CC} = \max\Big(0.0, 10.0 - (N_{\text{unexcused}} \times 2.0 + N_{\text{excused}} \times 1.0 + N_{\text{late}} \times 0.5)\Big)$$
  - Tự động phát hiện ngưỡng cảnh báo:
    - Vắng $10\% - 20\%$: Push Notification cảnh báo nguy cơ cấm thi đến sinh viên.
    - Vắng $> 20\%$: Đánh dấu `isBannedFromExam = true`, gán `attendanceScore = 0.0`, push Notification khẩn và cập nhật danh sách cấm thi.
- **RESTful API Controller (`AttendanceController.java`):**
  - 11 endpoints cung cấp đầy đủ các tác vụ cho Giảng viên, Sinh viên và Quản trị viên.
  - Tự động quét và đồng bộ thành công 200 API permissions vào vai trò `ADMIN`.
- **Frontend Giao diện Người Dùng (`react_tutorial/app`):**
  - Màn hình Điểm danh Giảng viên ([`teaching/attendance.tsx`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/react_tutorial/app/routes/teaching/attendance.tsx)): Chọn lớp, chọn buổi học, điểm danh nhanh với modal trực quan, xuất Excel danh sách buổi học.
  - Màn hình Tra cứu Chuyên cần Sinh viên ([`student/attendance.tsx`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/react_tutorial/app/routes/student/attendance.tsx)): 4 thẻ KPI, thanh Progress Bar tỷ lệ vắng $\%$, bảng nhật ký điểm danh từng buổi.
  - Màn hình Báo cáo Cấm thi Admin ([`admin/attendance-reports.tsx`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/react_tutorial/app/routes/admin/attendance-reports.tsx)): Danh sách sinh viên bị cấm thi toàn trường/theo môn, xuất Excel chuẩn báo cáo đào tạo.
  - Menu sidebar "Điểm danh & Chuyên cần" với icon `ClipboardCheckIcon` và phân quyền hiển thị theo Role.

### B. Tinh Chỉnh Giao Diện & Tối Ưu Trải Nghiệm (UI/UX Refinements)
- **Chuẩn hóa ActionDropdown ba chấm (`...`):**
  - Nâng cấp cơ chế `position: fixed` tính toán toạ độ viewport động (`getBoundingClientRect()`), giải quyết triệt để lỗi clipping khi bảng có thuộc tính `overflow-hidden`/`overflow-x-auto`.
  - Tự động nhận diện không gian hiển thị và lật menu ngược lên trên (`openUp`) khi menu ở cuối trang.
- **Tối ưu Popup Điểm danh:**
  - Thiết kế lại danh sách sinh viên tinh gọn: Chỉ hiển thị **Họ và tên** + **Mã sinh viên** kèm avatar ký tự đầu.
  - Chuyển cụm nút trạng thái thành **Dropdown chọn trạng thái gọn gàng** có chấm màu (`Có mặt`, `Đi muộn`, `Có phép`, `Vắng không phép`).
  - Hỗ trợ nhập số phút muộn linh hoạt khi chọn `Đi muộn`.
  - Bấm vào tên sinh viên mở **Popup Hồ sơ chi tiết sinh viên** (Email, SĐT, Lớp sinh hoạt, Ngành học, Giới tính, Ngày sinh, Địa chỉ, Trạng thái điểm danh hiện tại).
  - Tích hợp cả 2 nút **"Lưu & Tiếp tục"** và **"Chốt điểm danh"**, tự động đóng popup ngay sau khi lưu và cập nhật thông báo thành công.

---

---

## 5. Các công việc đã hoàn thành trong phiên làm việc ngày 10/09/2026 (Completed Work)

### A. Triển khai Toàn diện Tầng JPA Criteria Specification & Database Dynamic Filtering
- **Mở rộng `BaseRepository.java`:**
  - Kế thừa `JpaSpecificationExecutor<T>` trên `BaseRepository<T, ID>`, kích hoạt sẵn sàng khả năng thực thi Specification trên **100% Repository** trong dự án.
- **Tối ưu & Nâng cấp `BaseSpecification.java`:**
  - Cung cấp các helper dynamic predicate: `isNotDeleted()`, `likeIgnoreCase(attribute, value)`, `equalsIfNotNull(attribute, value)`, `inIfNotEmpty(attribute, collection)`, `keywordSearch(keyword, attributes...)` tránh `NullPointerException` và xây dựng câu lệnh SQL chuẩn xác.
- **Xây dựng bộ Specifications nghiệp vụ (`com.toan.university_management.specification.masterdata`):**
  - [`StudentSpecification.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/specification/masterdata/StudentSpecification.java): Lọc đa tiêu chí trực tiếp tại Database (từ khóa tổng hợp, mã SV, họ tên, email, ngành, lớp sinh hoạt, tỉnh/thành, quận/huyện, phường/xã).
  - [`TeacherSpecification.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/specification/masterdata/TeacherSpecification.java): Lọc theo mã GV, họ tên, email, SĐT, học vị, khoa, địa chỉ.
  - [`BuildingSpecification.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/specification/masterdata/BuildingSpecification.java): Lọc mã tòa nhà, tên tòa, trạng thái hoạt động.
  - [`ProvinceSpecification.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/specification/masterdata/ProvinceSpecification.java), [`DistrictSpecification.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/specification/masterdata/DistrictSpecification.java), [`WardSpecification.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/specification/masterdata/WardSpecification.java): Lọc danh mục hành chính địa giới.
- **Chuyển đổi tầng Service từ In-memory Filter sang Database-level Paging:**
  - Thay thế toàn bộ logic kéo toàn bộ bảng về RAM rồi lọc stream bằng `repository.findAll(spec, pageable)` trực tiếp.
  - Tối ưu hóa hiệu năng và bộ nhớ vượt trội, MySQL tự động thực thi mệnh đề `WHERE` và `LIMIT / OFFSET`.

---

## 6. Các công việc đã hoàn thành trong phiên làm việc ngày 17/09/2026 (Completed Work)

### A. Khắc phục Lỗi Kết nối Upstash Redis Cloud & Hỗ trợ `REDIS_URL`
- Nâng cấp [`RedisConfig.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/configuration/RedisConfig.java) và [`application.yml`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/resources/application.yml):
  - Hỗ trợ trực tiếp chuỗi `REDIS_URL` (dạng `rediss://default:token@host:port`).
  - Tự động nhận diện host Upstash (`*.upstash.io`) để bật `builder.useSsl()` ngay cả khi người dùng quên cấu hình `REDIS_SSL_ENABLED=true`.
  - Khắc phục triệt để lỗi `Redis CacheClear error: Unable to connect to Redis`.

### B. Khắc phục Triệt để Lỗi Reload Trang Bị Văng Ra Màn Login
- **Nguyên nhân:** Khối `.catch()` trong [`app-shell.tsx`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/react_tutorial/app/components/app-shell.tsx) bắt toàn bộ ngoại lệ (kể cả lỗi request bị browser abort khi bấm F5 reload) và lập tức gọi `clearToken()` xóa sạch token trong `localStorage`.
- **Giải pháp:**
  - Thêm cờ `isMounted` chống race condition khi unmount/reload.
  - Phân loại lỗi chặt chẽ: **CHỈ gọi `clearToken()` khi server thực sự trả về HTTP 401**.
  - Tích hợp cơ chế Stale-While-Revalidate: khi gặp lỗi mạng tạm thời hoặc server cold-start, tự động fallback vào cache `getCachedUser()`, giữ nguyên phiên đăng nhập.
  - Đồng bộ xử lý lỗi an toàn tại các màn hình: [`teaching/attendance.tsx`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/react_tutorial/app/routes/teaching/attendance.tsx), [`student/attendance.tsx`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/react_tutorial/app/routes/student/attendance.tsx), [`admin/attendance-reports.tsx`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/react_tutorial/app/routes/admin/attendance-reports.tsx).

### C. Triển khai Custom DatePicker Chuẩn Doanh nghiệp (Fixed DD/MM/YYYY Format)
- **Vấn đề:** Thẻ HTML native `<input type="date">` tự động đổi sang `mm/dd/yyyy` khi trình duyệt chuyển sang tiếng Anh Mỹ (`en-US`).
- **Giải pháp:**
  - Xây dựng component chuẩn [`DatePicker.tsx`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/react_tutorial/app/components/date-picker.tsx):
    - Cố định 100% định dạng hiển thị `dd/mm/yyyy` trên mọi trình duyệt/ngôn ngữ.
    - Hỗ trợ gõ phím trực tiếp với auto-slash mask (gõ số tự thêm `/`, tự validate ngày hợp lệ).
    - Popover Lịch tương tác đẹp mắt (chọn nhanh Năm sinh từ 1940-2035, chọn Tháng, nút Hôm nay, nút Xóa, hỗ trợ Dark/Light theme).
    - Giá trị `onChange` luôn xuất ra chuỗi chuẩn ISO `YYYY-MM-DD`, tương thích 100% với Database và Backend.
  - Thay thế toàn diện trên: Form Sinh viên (`student-form.tsx`), Form Lịch thi (`exam-schedule-form.tsx`), Modal Điểm danh (`attendance.tsx`).

### D. Rà soát Toàn diện Hệ thống & Xóa Bỏ Các Lỗi Nhập Liệu / Múi Giờ Cơ Bản
- **Khắc phục Lỗi Timezone Shift (Lệch ngày lùi 1 ngày):**
  - Cấu hình Jackson timezone toàn cục `spring.jackson.time-zone: Asia/Ho_Chi_Minh` trong [`application.yml`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/resources/application.yml).
  - Thêm `@JsonFormat(pattern = AppConstants.DATE_FORMAT, timezone = "Asia/Ho_Chi_Minh")` vào toàn bộ các DTO (`StudentRequest`, `StudentResponse`, `StudentSummary`, `StudentReportDTO`, `ExamScheduleResponse`, `AttendanceSessionResponse`, `StudentTuitionSummaryResponse`).
  - Nâng cấp `formatDate` và `dateValue` ở frontend: Parse trực tiếp chuỗi `YYYY-MM-DD` sang `DD/MM/YYYY` mà không gọi `new Date()`, triệt tiêu 100% rủi ro lệch múi giờ trên các trình duyệt quốc tế.
- **Khắc phục Form Input Jumps & Crash Edge Cases:**
  - Thay thế `value={val || fallback}` thành `val ?? fallback` tại `floor-form.tsx`, `building-form.tsx`, `room-form.tsx` để người dùng xóa Backspace thoải mái và nhập tầng 0 (tầng trệt) không bị ép thành 1.
  - Xử lý `maxStudents ? Number(maxStudents) : null` trong `class-group-form.tsx` tránh crash Jackson Integer Deserialization.
  - Bọc an toàn `formatTimeInput` và form submit trong `schedule-form.tsx`, `exam-schedule-form.tsx`.
- **Chuẩn hóa Hiển thị và Xuất Excel Ngày tháng:**
  - Đồng bộ `formatDate` và `formatDateTime` trên toàn bộ các route: `students.tsx`, `exam.tsx`, `teaching/attendance.tsx`, `student/attendance.tsx`, `tuition.tsx`, `course-registration.tsx`, `enrollment-management-modal.tsx`.

### E. Đồng bộ Hoàn chỉnh Git Workflow lên các Nhánh
- Toàn bộ commit đã được push đầy đủ và sạch sẽ lên cả 2 nhánh **`ToanDev`** và **`main`** cho cả 2 repository:
  - **Backend (`university-management`)**: Commit `0716e64` đồng bộ trên `origin/ToanDev` và `origin/main`.
  - **Frontend (`react_tutorial`)**: Commit `1bb6bb4` (merge clean) đồng bộ trên `origin/ToanDev` và `origin/main`.
  - Working tree hoàn toàn sạch (`working tree clean`).

---

## 7. Các công việc đã hoàn thành trong phiên làm việc ngày 23/09/2026 (Completed Work)

### A. Triển khai Khóa Hàng Bi Quan Chống Tranh Chấp Sĩ Số (Issue 2.1: Pessimistic Locking & Capacity Concurrency)
- **Vấn đề:** Kiểm tra sĩ số đăng ký tín chỉ là Check-Then-Act, khi nhiều sinh viên cùng đăng ký slot cuối cùng có thể dẫn tới vượt quá sĩ số lớp (`maxCapacity`).
- **Giải pháp:**
  - Bổ sung phương thức khóa dòng `@Lock(LockModeType.PESSIMISTIC_WRITE)` và câu lệnh `SELECT ... FOR UPDATE` trong `SubjectClassRepository.java` (`findByIdWithLock`).
  - Cập nhật `createEnrollment` trong `EnrollmentServiceImpl.java` sử dụng `findByIdWithLock` để khóa độc quyền hàng dữ liệu của lớp học phần trong suốt transaction.
  - Bảo vệ 100% cho cả luồng đăng ký cá nhân của sinh viên và phân bổ cả lớp sinh hoạt (`batchEnroll`).

### B. Tối Ưu Hóa Truy Vấn & Phân Trang Bảng Học Phí (Issue 2.2: Batch Loading & Database Paging in Tuition Module)
- **Vấn đề:** `getAllStudentsTuition` và `getDashboardSummary` trong `TuitionServiceImpl.java` kéo toàn bộ bảng Sinh viên về RAM, lặp qua từng sinh viên kích hoạt $O(4N)$ câu query con và phân trang in-memory bằng `subList`.
- **Giải pháp:**
  - Tích hợp `StudentSpecification.filter(rq)` vào `getAllStudentsTuition`: phân trang trực tiếp ở tầng Database với `LIMIT / OFFSET` khi không lọc status.
  - Xây dựng cơ chế gom nhóm dữ liệu (Batch Loading) `buildBatchStudentTuitionSummaries`: gom danh sách sinh viên của trang hiện tại và query `findAllByStudentIdInAndDeletedFalse`, `findAllByIdInAndDeletedFalse` chỉ trong 6 truy vấn SQL cố định (thay vì hàng nghìn query).
  - Tối ưu hóa cả `getDashboardSummary` bằng batch querying.

### C. Chuyển Đổi Phân Trang Danh Mục Quận/Huyện & Xã/Phường sang Database Paging (Issue 2.3)
- **Vấn đề:** `getAllDistricts` và `getAllWards` trong `DistrictServiceImpl.java` và `WardServiceImpl.java` sử dụng `subList()` trên RAM.
- **Giải pháp:**
  - Bổ sung các phương thức phân trang `Page<District>` và `Page<Ward>` có sắp xếp tên tăng dần trong `DistrictRepository.java` và `WardRepository.java`.
  - Thay thế toàn bộ việc cắt `subList` bằng truy vấn trực tiếp xuống Database.

---

## 8. Trạng thái kiểm tra & Xác thực (Verification Status)

- **Backend Tests:** `mvn test` $\longrightarrow$ **BUILD SUCCESS (49/49 PASS 100%, 0 failures, 0 errors)**.
- **Backend Compile:** `mvn test-compile` $\longrightarrow$ **BUILD SUCCESS (0 errors)**.
- **Frontend Build:** `npm run build` $\longrightarrow$ **BUILD SUCCESS in 2.09s (0 errors)**.

---

## 9. Hoàn thiện Bộ đôi Chức năng Quản lý Mật khẩu & Chuẩn hóa Log Console (Completed)

### A. Chuẩn hóa Log Console (Clean Enterprise Logs)
- Đã loại bỏ 100% emoji/icon trong toàn bộ Java source code (bao gồm Email Service, Aspect, Repositories, Controllers).
- Áp dụng format chuẩn Enterprise: `[AOP-EMAIL]`, `[DEV/FALLBACK ...]`, giúp log console sạch đẹp, không gây lỗi encoding hay font hiển thị trên các terminal/máy chủ.

### B. Chức năng Đổi Mật Khẩu (In-App Change Password)
- **Vấn đề trước đây:** Frontend gọi nhầm API admin `PUT /users/update`, khiến Sinh viên / Giảng viên bị `403 Forbidden` và không kiểm tra mật khẩu cũ.
- **Giải pháp hoàn thiện:**
  - Thêm ErrorCodes: `OLD_PASSWORD_INCORRECT(1026)`, `PASSWORD_SAME_AS_OLD(1027)`, `PASSWORD_CONFIRM_NOT_MATCH(1028)`.
  - DTO `ChangePasswordRequest`: `oldPassword`, `newPassword`, `confirmPassword`.
  - Service `changePassword` trong `UserServiceImpl`: trích xuất user hiện tại từ SecurityContext, so khớp mật khẩu cũ qua `passwordEncoder.matches()`, kiểm tra khớp mật khẩu mới, kiểm tra không trùng mật khẩu cũ, mã hóa và lưu.
  - Controller: `PUT /users/change-password` và `POST /users/change-password` (đã nằm trong danh sách `AUTHENTICATED_SELF_ENDPOINTS` của `DynamicApiAuthorizationManager`).
  - Frontend: Tích hợp modal "Đổi mật khẩu" trong `app-shell.tsx`, thêm trường nhập mật khẩu hiện tại, bắt lỗi chi tiết.
  - Automated Tests: `ChangePasswordTest.java` (4/4 PASS).

### C. Chức năng Quên Mật Khẩu (Forgot / Reset Password via Email OTP)
- Cơ chế bảo mật: OTP 6 số ngẫu nhiên lưu trên Upstash Redis với TTL 5 phút.
- Luồng xử lý: Gửi OTP bất đồng bộ qua `EmailService` với AOP Aspect không bị block transaction.
- Kiểm thử tự động: `ResetPasswordFlowTest.java` xác thực trọn vẹn luồng từ request OTP đến đặt lại mật khẩu mới (2/2 PASS).

---

## 10. Vấn Đề Lưu Ý & Hướng Dẫn Tiếp Tục Phát Triển (Pending Issues & Next Steps)

1. **Bật lại mã hóa BCrypt khi lên môi trường Production:** Đổi dòng return trong [`PasswordEncoderConfig.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/configuration/PasswordEncoderConfig.java) thành `return new BCryptPasswordEncoder(10);`.
2. **Chi tiết lộ trình tính năng tiếp theo:** Tham khảo file [`ROADMAP.md`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/ROADMAP.md) (Ràng buộc môn học tiên quyết, Khung chương trình đào tạo, Cổng thanh toán trực tuyến).
3. Luôn chạy `mvn test` và `npm run build` để kiểm tra tính toàn vẹn hệ thống trước mỗi lần bàn giao.



