# CONTEXT.md — Ngữ cảnh hiện tại dự án

> **Loại**: Tài liệu sống — cập nhật cuối mỗi phiên làm việc hoặc khi hoàn thành plan.  
> **Cập nhật lần cuối**: 2026-08-12 17:40

---

## Trạng thái tổng thể: 🟢 Backend & Frontend BUILD SUCCESS 100% — Refactor Subject/SubjectClass & Numeric ID Migration hoàn tất

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
| Redis config        | ✅ Done     | Token blacklist storage                           |
| Entity layer        | ✅ Done     | 19 Entities: 13 Masterdata (`Long IDENTITY`) + 5 Identity (`String UUID`) + 1 Auth (`String manual`) |
| Repository layer    | ✅ Done     | 15+ Repositories với `existsByIdAndDeletedFalse`, `findAllByIdInAndDeletedFalse` |
| Service layer       | ✅ Done     | Interfaces + Implementations với Batch DTO Assembly (chống N+1 Query) |
| Controller layer    | ✅ Done     | 12 REST Controllers với CRUD + Pageable endpoints |
| DTO + MapStruct     | ✅ Done     | Request/Response DTOs, Mappers phẳng hóa          |
| Exception handling  | ✅ Done     | GlobalExceptionHandler, ErrorCode enum, ApiResponse|
| JWT Authentication  | ✅ Done     | Login, logout, refresh token                      |
| Token blacklist     | ✅ Done     | Redis-based, TokenBlacklistFilter                 |
| Dynamic API PBAC    | ✅ Done     | EndpointAutoScanner + DynamicApiAuthorizationManager |
| Public Perms Cache  | ✅ Done     | Spring `@EnableCaching`, `@Cacheable`, `@CacheEvict` |
| Default Roles Init  | ✅ Done     | Tự khởi tạo `ROLE_ADMIN`, `ROLE_USER`, `ROLE_TEACHER`, `ROLE_STUDENT` |
| Admin initializer   | ✅ Done     | Tạo tài khoản Admin/Teacher/Student mặc định      |
| Soft Delete (Xóa mềm)| ✅ Done     | Thêm cột `deleted` và Hibernate `@SQLDelete`, `@SQLRestriction` cho Masterdata |
| Automated Test Suite| ✅ Done     | `./mvnw test` → **9/9 TESTS PASSED 100%** |
| Skills Integration  | ✅ Done     | Cài đặt 9 Agentic Skills từ Pocock set vào `.agents/skills/` |
| React FE Type Sync  | ✅ Done     | Đồng bộ TypeScript types với backend DTOs |

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

### 2026-08-12 (Phiên chiều — Refactor Course/CourseClass sang Subject/SubjectClass)

1. **Refactor SubjectClass Entity & Layer**:
   - Tạo mới `SubjectClass` entity (`@Table(name = "subject_class")`), `SubjectClassRepository`, `SubjectClassService`, `SubjectClassServiceImpl`, `SubjectClassController`, `SubjectClassRequest`, `SubjectClassResponse`, `SubjectClassMapper`.
   - Xóa bỏ toàn bộ các file legacy `CourseClass...` cũ.
2. **Cập nhật các liên kết Khóa ngoại Logic**:
   - `ClassSchedule`, `Enrollment`, `ExamSchedule`: Chuyển trường & cột từ `course_class_id` (`courseClassId`) sang `subject_class_id` (`subjectClassId`).
   - Cập nhật index và unique constraints tương ứng (`uk_enrollment_student_class_deleted`, `idx_schedule_subject_class`...).
3. **Hỗ trợ Tương thích ngược (Backward Compatibility)**:
   - Thêm `@JsonAlias` và `@JsonProperty` trong `SubjectClassRequest`, `SubjectClassResponse`, `ClassScheduleResponse`, `EnrollmentResponse`, `ExamScheduleResponse` giúp Frontend gọi API bằng key cũ (`courseClassId`, `courseClassCode`) hoặc key mới đều hoạt động bình thường.
4. **MasterData Service & Dynamic Mapping**:
   - Cập nhật `MasterDataServiceImpl.java` hỗ trợ lấy combo datasource cho `SUBJECT_CLASS` và `COURSE_CLASS`.
5. **Biên dịch & Push**:
   - `./mvnw test-compile` → **BUILD SUCCESS 100%** (184 source files compiled).
   - Git Commit & Push thành công lên branch `ToanDev` (`origin/ToanDev`).

### 2026-08-11 (Phiên chiều — Numeric ID Migration & FE Sync)

1. **Chuyển đổi ID sang số**: 13 masterdata entities từ `String UUID` → `Long IDENTITY`.
2. **Sửa FK type mismatch `ExamSchedule`**: `courseClassId` & `proctorId` từ `String` → `Long` (Entity + Request DTO + Response DTO).
3. **Xóa `CourseController.java`** trùng mapping `/courses` với `CourseClassController.java`.
4. **Auto DB cleanup** (`UniversityManagementApplication.java`):
   - Phát hiện legacy VARCHAR PK → drop tables migrate BIGINT.
   - So sánh tất cả bảng với `KNOWN_TABLES` (19 bảng) → xóa bảng thừa (VD: `course`).
5. **Review & sửa 8 file React Frontend**:
   - `types/schedule.ts`: ID → `number|string`, `courseId` → `courseClassId`, xóa `classGroupId`.
   - `types/student.ts`: `id`, `classGroupId` → `number|string`.
   - `types/management.ts`: Thêm `Subject` type, bổ sung fields `ClassSchedule`.
   - `timetable.tsx`: Đồng bộ field names với backend.
   - `schedule-form.tsx`: `courseId` → `courseClassId`, xóa classGroup dropdown.
   - `course-form.tsx` & `courses.tsx`: `specialization` → `degree`.
   - `exam-schedule-form.tsx`: Fetch `/subjects/all` thay vì `/courses/all`.
6. **Build verification**: Backend 184 files ✅ | Frontend client+server ✅

### 2026-08-11 (Phiên sáng)
- Review và Thực thi Test Suite toàn diện.
- Khắc phục lỗi JPQL constructor query cho `StudentReportDTO` trong `StudentRepository.java`.
- Cập nhật test cases cho `SoftDeleteTest` và `PaginationAndEntityGraphTest`.
- Chạy toàn bộ test suite: `./mvnw test` → **BUILD SUCCESS (9/9 tests passed)**.
