# Lộ Trình Phát Triển & Định Hướng Mở Rộng Hệ Thống (Roadmap & Feature Extensions)

*Tài liệu định hướng nâng cấp, backlog tính năng và kế hoạch phát triển dự án University Management*  
*Cập nhật: 11/09/2026*

---

## 1. Các Tính Năng Đã Hoàn Thành (Completed Modules & Highlights)

### A. Nhóm Nghiệp Vụ Đào Tạo Cốt Lõi (Core Academic)
- [x] **1.1. Quản lý Điểm & Bảng điểm sinh viên (Grade & Transcript Management):**
  - Nhập điểm học phần, biểu đồ phổ điểm trực quan, chuyển trạng thái bảng điểm (`DRAFT -> SUBMITTED -> PUBLISHED -> LOCKED`), tính GPA/CPA thang 10 và thang 4, xếp loại học lực, xuất Excel.
- [x] **1.2. Cổng Đăng ký Tín chỉ Trực tuyến (Student Course Registration):**
  - Đăng ký/hủy môn học, check xung đột lịch học (`ClassSchedule`), check sĩ số tối đa, giới hạn tối đa 24 TC/kỳ, gán nhanh lớp sinh hoạt vào lớp học phần.
- [x] **1.3. Quản lý Tài chính & Học phí Sinh viên (Tuition & Financial Management):**
  - Biểu giá tín chỉ, phiếu báo học phí cá nhân chi tiết từng môn, Dashboard KPI tài chính (Tổng phát sinh, Thực thu, Công nợ, Tỷ lệ thu hồi %), ghi nhận thu tiền mặt/POS/chuyển khoản, xuất Excel.
- [x] **1.4. Quản lý Điểm danh & Cảnh báo Chuyên cần (Attendance & Absence Warning):**
  - Tự động sinh danh sách buổi học theo TKB, điểm danh 1-click, tự động tính điểm chuyên cần thang điểm 10, gửi Notification cảnh báo nguy cơ ($10\% - 20\%$) và áp dụng chế tài **CẤM THI** khi vắng $> 20\%$, báo cáo cấm thi toàn trường.
- [x] **1.5. Thời khóa biểu & Lịch thi Matrix View:**
  - 3 chế độ xem (*Ma trận Tuần, Cột 7 Ngày, Dạng Bảng*), bộ lọc học kỳ/năm học, cơ chế tìm kiếm 4-tier fallback (`userId -> username -> userCode -> email`), **tích hợp Skeleton Loading mượt mà chống giật layout**.

### B. Nhóm Kỹ Thuật & Kiến Trúc (Architecture & Technical)
- [x] **1.6. Tầng Base Entity & JPA Audit Trail Toàn Hệ Thống:**
  - Kế thừa `BaseEntity` trên 100% Entity (23/23 bảng), tự động lưu vết `createdAt`, `createdBy`, `updatedAt`, `updatedBy` từ Spring Security Context qua `AuditorAware`.
- [x] **1.7. Lọc Động Trực Tiếp Tại Database với JPA Criteria Specification:**
  - Mở rộng `BaseRepository extends JpaSpecificationExecutor<T>`, nâng cấp `BaseSpecification`, xây dựng `StudentSpecification`, `TeacherSpecification`, `BuildingSpecification`, `ProvinceSpecification`, `DistrictSpecification`, `WardSpecification`.
  - Chuyển đổi toàn bộ search service sang Database-level Paging (`LIMIT/OFFSET`).
- [x] **1.8. Cấp Tài Khoản & Gửi Mail Chào Mừng Tự Động:**
  - Tự động tạo `User` tương ứng khi thêm Sinh viên/Giảng viên, gửi email template HTML chứa mật khẩu khởi tạo, đảm bảo Atomic Transaction Rollback 100%. Khôi phục mật khẩu an toàn bằng mã OTP qua email.
- [x] **1.9. Phân Quyền Endpoint Động & Dashboard Cá Nhân Hóa (Dynamic RBAC & Role-based Dashboard):**
  - **Permission Engine (`lib/permission.ts`)**: Khớp quyền theo endpoint Ant-Path pattern (`/**`, `/*`, `{id}`) & HTTP method (`GET`, `POST`, v.v.).
  - **Action & Route Guard (`PermissionGate`, `ForbiddenState`)**: Tự động chặn truy cập nút bấm và URL trái phép với trang 403 Access Denied.
  - **Dynamic Smart Menu (`AppShell`)**: Ẩn/hiện danh mục và menu con theo quyền thực tế của User.
  - **Dashboard Đa Vai Trò (`home.tsx`)**: Giao diện thích ứng với 4 Sub-dashboards (`StudentDashboard`, `TeacherDashboard`, `AdminDashboard`, `AdaptiveGenericDashboard` cho các vai trò mới được tạo).

---

## 2. Backlog Các Task Đề Xuất Phát Triển Tiếp Theo (Future Enhancement Tasks)

### 🚀 Giai Đoạn 1: Trải Nghiệm Người Dùng & Dashboard Cá Nhân Hóa (UI/UX & Analytics)
> *Mục tiêu: Nâng cao tính trực quan và mang lại trải nghiệm chuyên nghiệp theo từng vai trò người dùng.*

- [x] **Task 1.1: Dashboard Cá Nhân Hóa Theo Vai Trò & Phân Quyền Endpoint Động (Role-based Portal Experience)** - *[HOÀN THÀNH]*

  - **Sinh viên (`/`):**
    - Widget *"Lịch học hôm nay"* (Thời gian, Ca học, Tên môn, Phòng học, Tên giảng viên).
    - Widget *"Cảnh báo Học vụ & Chuyên cần"* (Hiển thị các môn có tỷ lệ vắng $>10\%$ kèm cảnh báo đỏ).
    - Widget *"Học phí cần nộp"* (Công nợ hiện tại và nút chuyển nhanh đến thanh toán).
    - Biểu đồ biến động GPA/CPA qua các học kỳ (Line Chart).
  - **Giảng viên (`/`):**
    - Widget *"Lớp cần điểm danh hôm nay"* và *"Học phần chưa nộp bảng điểm"*.
    - Widget *"Lịch giảng dạy trong tuần"*.
    - Biểu đồ phân bổ phổ điểm các lớp đang phụ trách (Histogram A, B, C, D, F).
  - **Admin / Đào tạo (`/`):**
    - Biểu đồ phân bổ sinh viên theo Ngành / Khoa (Pie/Donut Chart).
    - Biểu đồ tỷ lệ thu hồi học phí toàn trường theo tháng (Bar Chart).
    - Thống kê tỷ lệ lấp đầy phòng học theo khung giờ.

- [ ] **Task 1.2: Thanh Tìm Kiếm Nhanh Toàn Hệ Thống (Command Palette `Ctrl + K` / `Cmd + K`) - [ƯU TIÊN TRUNG BÌNH]**
  - Bấm phím tắt `Ctrl + K` ở bất cứ màn hình nào để mở popup tìm kiếm tức thì.
  - Tìm kiếm nhanh sinh viên (gõ mã SV/tên $\rightarrow$ mở hồ sơ chi tiết).
  - Tìm kiếm nhanh môn học, lớp học phần, phòng học.
  - Điều hướng tắt đến các chức năng mà không cần click sidebar.

- [ ] **Task 1.3: Nâng Cấp Hệ Thống Toast Thông Báo (Sonner / Micro-interactions) - [ƯU TIÊN TRUNG BÌNH]**
  - Thay thế các alert tĩnh bằng thư viện Toast trượt nhẹ hiện đại.
  - Hỗ trợ Undo action nhanh (Ví dụ: *"Đã xóa sinh viên - Bấm Hoàn tác"*).

---

### 🎓 Giai Đoạn 2: Nghiệp Vụ Đào Tạo Nâng Cao (Advanced Academic Domain)
> *Mục tiêu: Đạt chuẩn quy trình đào tạo tín chỉ của các trường đại học lớn.*

- [ ] **Task 2.1: Ràng Buộc Môn Học Tiên Quyết (Prerequisite Course Engine) - [ƯU TIÊN CAO]**
  - Tạo bảng quan hệ `SubjectPrerequisite` (Môn học $\leftrightarrow$ Môn tiên quyết).
  - Khi sinh viên đăng ký môn học trong `/course-registration`, backend kiểm tra sinh viên đã qua môn tiên quyết chưa (Điểm tổng kết môn trước $\ge 4.0$).
  - Giao diện sơ đồ cây môn học điều kiện trực quan trên trang Môn học.

- [ ] **Task 2.2: Khung Chương Trình Đào Tạo & Xét Tốt Nghiệp (Degree Audit & Curriculum) - [ƯU TIÊN TRUNG BÌNH]**
  - Mỗi Ngành học thiết lập Khung chương trình (VD: 145 tín chỉ: Đại cương, Cơ sở ngành, Chuyên ngành, Tốt nghiệp).
  - Màn hình theo dõi tiến độ tích lũy tín chỉ cá nhân của sinh viên (% hoàn thành, các môn bắt buộc còn thiếu).

- [ ] **Task 2.3: Phân Đợt Đăng Ký Tín Chỉ Đa Cấp (Registration Rounds & Waves) - [ƯU TIÊN TRUNG BÌNH]**
  - Tạo cấu hình đợt đăng ký: Đợt 1 (Sinh viên đúng tiến độ), Đợt 2 (Học vượt/cải thiện), Đợt 3 (Hủy/chốt môn).
  - Khóa/mở cổng đăng ký tự động theo thời gian thực (Start Time - End Time).

---

### ⚡ Giai Đoạn 3: Kiến Trúc Nâng Cao & Tối Ưu Hệ Thống (Architecture & Concurrency)
> *Mục tiêu: Hệ thống chịu tải cao, đồng bộ dữ liệu mượt mà.*

- [ ] **Task 3.1: Kiến Trúc Hướng Sự Kiện Nội Bộ (Spring ApplicationEventPublisher) - [ƯU TIÊN CAO]**
  - Tách rời các side-effects khỏi nghiệp vụ chính:
    - Khi `StudentEnrolledEvent` bắn ra $\rightarrow$ Async Listener tự động tính học phí và gửi Notification.
    - Khi `GradePublishedEvent` bắn ra $\rightarrow$ Async Listener tự động cập nhật CPA/GPA và gửi email thông báo điểm.

- [ ] **Task 3.2: Bộ Nhớ Đệm Đa Tầng Caching (Redis / Caffeine Cache) - [ƯU TIÊN TRUNG BÌNH]**
  - Cấu hình `@Cacheable` cho các dữ liệu danh mục tĩnh (Tòa nhà, Phòng học, Khoa, Ngành, Địa giới).
  - `@CacheEvict` tự động dọn sạch cache khi Admin tạo/sửa/xóa.

- [x] **Task 3.3: Xử Lý Xung Đột & Tranh Chấp Sĩ Số Khi Đăng Ký Môn (Pessimistic Locking / SELECT FOR UPDATE) - [HOÀN THÀNH 23/09/2026]**
  - Đã tích hợp `@Lock(LockModeType.PESSIMISTIC_WRITE)` vào `SubjectClassRepository.findByIdWithLock` và áp dụng cho `createEnrollment`, loại bỏ hoàn toàn nguy cơ vượt sĩ số lớp (`currentCapacity > maxCapacity`).

- [ ] **Task 3.4: Triển Khai Dịch Vụ Gửi Email Thật (Production Real-Email SMTP Integration) - [ƯU TIÊN CAO]**
  - **Mục tiêu:** Chuyển đổi từ Mailtrap Sandbox (chỉ bắt email ảo trong môi trường dev) sang gửi email thật ra Internet để người dùng thực tế nhận được thư kích hoạt tài khoản / OTP khôi phục mật khẩu.
  - **Phương án 1 (Khuyên dùng - Brevo / Sendinblue):**
    - Cấu hình SMTP: `smtp-relay.brevo.com`, port `587`.
    - Đăng ký tài khoản Brevo miễn phí (hạn mức 300 emails thật/ngày), tạo SMTP Key và cấu hình vào backend. Không phụ thuộc vào tài khoản cá nhân.
  - **Phương án 2 (Gmail SMTP cá nhân):**
    - Bật 2FA tài khoản Google cá nhân $\rightarrow$ tạo App Password 16 ký tự tại `https://myaccount.google.com/apppasswords`.
    - Cấu hình SMTP: `smtp.gmail.com`, port `587`, username = email cá nhân, password = 16 ký tự app password.
  - **Biến môi trường cần chuẩn hóa trong `application.yml`:**
    - `${MAIL_HOST}`, `${MAIL_PORT}`, `${MAIL_USERNAME}`, `${MAIL_PASSWORD}`, `${MAIL_FROM_EMAIL}`.

- [ ] **Task 3.5: Mở Rộng Kiến Trúc Với Message Queue (RabbitMQ / Redis Queue) - [ĐÃ THẢO LUẬN - LƯU ĐỂ BÀN LẠI]**
  - **3 Phân hệ áp dụng chính:**
    1. *Hàng đợi Email & Push Notification (Dễ làm, giá trị tức thì):* Bất đồng bộ hóa gửi mail qua Brevo/SMTP, cơ chế Retry tự động + Dead-Letter Queue (DLQ).
    2. *San phẳng đỉnh tải Đăng ký Tín chỉ (Traffic Shaping - Giá trị kiến trúc cao nhất):* Trừ slot siêu nhanh bằng Redis $\rightarrow$ Đẩy yêu cầu vào Queue $\rightarrow$ Trả về trạng thái Pending $\rightarrow$ Worker bốc tuần tự ghi DB (chống deadlock/sập DB) $\rightarrow$ Bắn WebSocket báo kết quả.
    3. *Kiến trúc Hướng sự kiện (Event-Driven):* Tách rời luồng Chốt điểm $\rightarrow$ Tính GPA $\rightarrow$ Học bổng $\rightarrow$ Gửi mail.
  - **Lựa chọn công nghệ (RabbitMQ vs Kafka):**
    - **Ưu tiên RabbitMQ:** Nhẹ (~150MB RAM), có sẵn UI quản trị, hỗ trợ DLQ/Delay Queue chuẩn chỉ, tích hợp dễ với Spring Boot (`@RabbitListener`).
    - **Không khuyến nghị Kafka:** Quá cồng kềnh cho quy mô trường ĐH (ngốn 1-2GB RAM, thiếu các tính năng task queue built-in, over-engineering).
  - **Lộ trình 3 bước thực chiến:**
    - *Bước 1 (Đã hoàn thành 24/09/2026):* Triển khai AOP Aspect (`EmailAsyncTransactionAspect` + `@AsyncTransactionalEmail` + `mailTaskExecutor`). Giải quyết triệt để 100% nghẽn mail HTTP (< 30ms) và hook an toàn `afterCommit` với DB Transaction.
    - *Bước 2 (Tận dụng vũ khí có sẵn):* Dùng Redis Upstash hiện tại làm hàng đợi (Redis List / Redis Streams).
    - *Bước 3 (Nâng tầm đồ án):* Triển khai RabbitMQ hoàn chỉnh khi cần làm điểm sáng công nghệ (Wow factor) bảo vệ trước hội đồng.

---

### 🌟 Giai Đoạn 4: Tính Năng Điểm Nhấn Đột Phá (Technology Highlights / "Wow" Factors)
> *Mục tiêu: Điểm cộng xuất sắc khi báo cáo đồ án / review hội đồng.*

- [ ] **Task 4.1: Thông Báo Thời Gian Thực qua WebSocket / SSE (Realtime Notification) - [ƯU TIÊN CAO]**
  - Tích hợp Spring WebSocket (STOMP) hoặc Server-Sent Events (SSE).
  - Biểu tượng chuông thông báo trên Header tự động nhảy badge đỏ và phát âm thanh/popup tức thì khi có thông báo mới mà không cần F5/polling.

- [ ] **Task 4.2: Tích Hợp Cổng Thanh Toán Học Phí Trực Tuyến Sandbox (VNPay / MoMo QR) - [ƯU TIÊN CAO]**
  - Sinh mã thanh toán QR Code VNPay Sandbox trên giao diện học phí sinh viên.
  - Xử lý IPN / Webhook callback từ cổng thanh toán $\rightarrow$ tự động chuyển trạng thái học phí thành `PAID` và xuất biên lai điện tử.

- [ ] **Task 4.3: Trợ Lý Ảo AI Học Vụ (AI Academic Assistant - Gemini API) - [ƯU TIÊN TRUNG BÌNH]**
  - Widget chatbox góc màn hình kết nối Google Gemini API.
  - Hỗ trợ sinh viên tra cứu quy chế, hỏi lịch học/lịch thi trong ngày, giải đáp thủ tục học vụ bằng ngôn ngữ tự nhiên.

- [ ] **Task 4.4: Import / Export Dữ Liệu Hàng Loạt Bằng Excel (Apache POI Bulk Processor) - [ƯU TIÊN TRUNG BÌNH]**
  - Tải file Excel mẫu (*Template*) và import hàng loạt danh sách Sinh viên, Giảng viên, Phòng học, Lịch thi kèm kiểm tra và hiển thị chi tiết dòng lỗi.

---

## 3. Checklist Chuẩn Bị Trước Giờ Review / Demo (Pre-Review Checklist)

- [x] **Dữ liệu mẫu phong phú (Rich Seed Data):** `AdminInitializer.java` tự động khởi tạo dữ liệu mẫu hoàn chỉnh (Khoa, Ngành, Môn học, Lớp học phần, Tòa nhà, Tầng, Phòng học, Thời khóa biểu, Lịch thi, Lớp sinh viên).
- [x] **Tài khoản Demo sẵn sàng:**
  - Quản trị viên: `admin` / `admin`
  - Giảng viên: `teacher` / `teacher123`
  - Sinh viên: `student` / `student123`
- [x] **Độ ổn định & Kiểm thử:**
  - `npm run build` $\longrightarrow$ **100% BUILD SUCCESS (0 errors)**.
  - `mvn clean test` $\longrightarrow$ **29/29 tests PASS 100% (0 errors)**.
