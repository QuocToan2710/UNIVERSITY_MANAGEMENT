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

## 5. Trạng thái kiểm tra & Xác thực (Verification Status)

- **Backend Tests:** `mvn test -Dtest=*ApplicationTests*` $\longrightarrow$ **BUILD SUCCESS (100% PASS, 0 errors, Auto-synced 200 API permissions to ADMIN)**.
- **Backend Compile:** `mvn clean test-compile` $\longrightarrow$ **BUILD SUCCESS (0 errors)**.
- **Frontend Build:** `npm run build` $\longrightarrow$ **BUILD SUCCESS in 2.08s (0 errors)**.

---

## 6. Hướng dẫn tiếp tục phát triển & Định hướng mở rộng (Next Steps & Roadmap)

1. **Bật lại mã hóa BCrypt khi lên môi trường Production:** Chỉ cần đổi dòng return trong [`PasswordEncoderConfig.java`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/university-management/src/main/java/com/toan/university_management/configuration/PasswordEncoderConfig.java) thành `return new BCryptPasswordEncoder(10);`.
2. **Chi tiết lộ trình tính năng tiếp theo:** Tham khảo file [`ROADMAP.md`](file:///D:/My%20Project/UNIVERSITY_MANAGEMENT/ROADMAP.md) (Tích hợp cổng thanh toán trực tuyến VNPay/MoMo Sandbox cho học phí, Import Excel danh sách lớp, WebSocket thông báo realtime, AI Assistant).
3. Luôn chạy `mvn test` và `npm run build` để kiểm tra tính toàn vẹn hệ thống trước mỗi lần bàn giao.


