# 📐 Thiết kế Database Chi tiết — Application-Level Relationship Architecture

> **Dự án**: University Management System (Spring Boot 3.5 + React Router v7)  
> **Tài liệu**: Proposal & Detailed Database Specification (Mô hình Khóa ngoại Logic — No Database FK Constraints)  
> **Trạng thái**: Đã cập nhật đầy đủ 15 Bảng — Đồng bộ 100% Quy chuẩn `code` và `name`  
> **Ngày cập nhật**: 2026-08-11

---

## 📑 MỤC LỤC
1. [Triết lý & Quy chuẩn Thiết kế (Design Rules)](#1-triết-lý--quy-chuẩn-thiết-kế-design-rules)
2. [Bảng So sánh Thiết kế Cũ vs Thiết kế Mới](#2-bảng-so-sánh-thiết-kế-cũ-vs-thiết-kế-mới)
3. [Biểu đồ ERD Tổng quan (Logical ERD)](#3-biểu-đồ-erd-tổng-quan-logical-erd)
4. [Chi tiết 15 Bảng Cơ sở Dữ liệu](#4-chi-tiết-15-bảng-cơ-sở-dữ-liệu)
   - 4.1 [Phân hệ Identity & Security](#41-phân-hệ-identity--security-5-bảng)
   - 4.2 [Phân hệ Danh mục Đào tạo (Masterdata)](#42-phân-hệ-danh-mục-đào-tạo-masterdata-6-bảng)
   - 4.3 [Phân hệ Lớp học phần & Đăng ký & Điểm số](#43-phân-hệ-lớp-học-phần--đăng-ký--điểm-số-3-bảng)
   - 4.4 [Phân hệ Auth Token Blacklist](#44-phân-hệ-auth-token-blacklist-1-bảng)
5. [Chiến lược Đánh Index Tối ưu trên MySQL](#5-chiến-lược-đánh-index-tối-ưu-trên-mysql)
6. [Quy tắc Xử lý Logic ở Tầng Java Service Layer](#6-quy-tắc-xử-lý-logic-ở-tầng-java-service-layer)
7. [Mẫu Code Java Entity & Service Assembler](#7-mẫu-code-java-entity--service-assembler)
8. [Lộ trình Triển khai (Migration Roadmap)](#8-lộ-trình-triển-khai-migration-roadmap)

---

## 1. 🎯 Triết lý & Quy chuẩn Thiết kế (Design Rules)

Tất cả 15 Entity/Bảng trong hệ thống tuân thủ nghiêm ngặt **3 Quy chuẩn Cốt lõi**:

1. **Quy chuẩn Định danh Đồng bộ (`code` & `name`)**:
   - **`code`** (Mã nghiệp vụ duy nhất): Dùng để định danh, tra cứu nhanh, import/export dữ liệu. Cột `code` được đánh `UNIQUE` constraint (kèm `deleted`).
   - **`name`** (Tên hiển thị): Dùng cho UI, báo cáo, hiển thị cho người dùng.
2. **Khóa ngoại Logic (Application-Level Relationships)**:
   - **TẮT HOÀN TOÀN câu lệnh `CONSTRAINT FOREIGN KEY` ở MySQL**.
   - Các bảng con chỉ lưu ID dạng String đại diện (`user_id`, `student_id`, `department_id`...).
   - Loại bỏ 100% rủi ro Lock bảng, Deadlock, và xung đột khi thao tác hàng loạt.
3. **Tương thích 100% với Soft Delete (`deleted = true`)**:
   - Mọi kiểm tra ràng buộc tồn tại được xử lý ở tầng Java Service với điều kiện `deleted = false`.

---

## 2. 🔄 Bảng So sánh Thiết kế Cũ vs Thiết kế Mới

| Hạng mục | Thiết kế Cũ (Hiện tại) | Thiết kế Mới (Đề xuất) | Lợi ích Mang lại |
|---|---|---|---|
| **Khoá ngoại** | Cứng (`@ManyToOne`, `@ManyToMany` tự sinh FK DB) | Logic (`String <entity>Id`, `foreignKey = NO_CONSTRAINT`) | Xóa mềm không bao giờ bị lỗi DB violation, chống deadlock |
| **Môn học & Lớp** | Nhầm lẫn `Course` chung cho ca dạy và môn học | Tách thành `Subject` (Môn) và `CourseClass` (Lớp HP theo HK) | Phản ánh đúng thực tế đăng ký môn học tại các trường đại học |
| **Đăng ký & Điểm** | `@ManyToMany` 2 cột ngầm, không lưu được điểm | Entity `Enrollment` lưu điểm quá trình, điểm thi, ngày đăng ký | Hỗ trợ nhập điểm, tính GPA, cấp chứng chỉ |
| **Liên kết User** | `User` độc lập với `Student`/`Teacher` | Thêm `user_id` vào `Student` và `Teacher` | Xác định ngay hồ sơ người dùng sau khi Đăng nhập |
| **Định danh Bảng** | Mỗi bảng một kiểu đặt tên | Đồng bộ 100% bảng đều có `code` và `name` | Codebase sạch, chuẩn hóa DTO Mapper |

---

## 3. 📊 Biểu đồ ERD Tổng quan (Logical ERD)

```mermaid
erdiagram
    USER ||--o| STUDENT : "user_id (Logic)"
    USER ||--o| TEACHER : "user_id (Logic)"
    USER ||--o{ USER_ROLE : "user_id (Logic)"
    ROLE ||--o{ USER_ROLE : "role_code (Logic)"
    ROLE ||--o{ ROLE_PERMISSION : "role_code (Logic)"
    PERMISSION ||--o{ ROLE_PERMISSION : "permission_code (Logic)"

    DEPARTMENT ||--o{ TEACHER : "department_id (Logic)"
    DEPARTMENT ||--o{ MAJOR : "department_id (Logic)"
    MAJOR ||--o{ CLASS_GROUP : "major_id (Logic)"
    MAJOR ||--o{ STUDENT : "major_id (Logic)"

    TEACHER ||--o{ CLASS_GROUP : "homeroom_teacher_id (Logic)"
    CLASS_GROUP ||--o{ STUDENT : "class_group_id (Logic)"

    SUBJECT ||--o{ COURSE_CLASS : "subject_id (Logic)"
    TEACHER ||--o{ COURSE_CLASS : "teacher_id (Logic)"

    COURSE_CLASS ||--o{ ENROLLMENT : "course_class_id (Logic)"
    STUDENT ||--o{ ENROLLMENT : "student_id (Logic)"
    COURSE_CLASS ||--o{ CLASS_SCHEDULE : "course_class_id (Logic)"
```

---

## 🗄️ 4. Chi tiết 15 Bảng Cơ sở Dữ liệu

### 4.1. Phân hệ Identity & Security (5 Bảng)

#### 1. Bảng `user` (Tài khoản người dùng)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `user_code` | `VARCHAR(36)` | **Code** | Mã định danh tài khoản (`USR001`, `USR_ADMIN`) *(Unique)* |
| `name` / `full_name` | `VARCHAR(100)` | **Name** | Tên hiển thị người dùng (`Nguyễn Văn A`) |
| `username` | `VARCHAR(50)` | Field | Tên đăng nhập (`admin`, `toan01`) *(Unique)* |
| `password` | `VARCHAR(255)` | Field | Mật khẩu mã hóa BCrypt |
| `email` | `VARCHAR(100)` | Field | Email liên hệ |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

#### 2. Bảng `role` (Vai trò)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `role_code` | `VARCHAR(50)` | **Code** | Mã vai trò (`ROLE_ADMIN`, `ROLE_TEACHER`, `ROLE_STUDENT`) *(Unique)* |
| `name` | `VARCHAR(100)` | **Name** | Tên hiển thị vai trò (`Quản trị viên hệ thống`) |
| `description` | `VARCHAR(255)` | Field | Mô tả chi tiết chức năng vai trò |

#### 3. Bảng `user_role` (Bảng trung gian Gán Role)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `user_id` | `VARCHAR(36)` | Logical FK | ID tài khoản $\rightarrow$ `user(id)` *(Index)* |
| `role_code` | `VARCHAR(50)` | Logical FK | Mã vai trò $\rightarrow$ `role(role_code)` *(Index)* |

#### 4. Bảng `permission` (Quyền thao tác API)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `permission_code` | `VARCHAR(100)` | **Code** | Mã quyền (`PERM_STUDENT_CREATE`, `PERM_COURSE_DELETE`) *(Unique)* |
| `name` | `VARCHAR(100)` | **Name** | Tên hiển thị quyền (`Tạo mới hồ sơ Sinh viên`) |
| `description` | `VARCHAR(255)` | Field | Mô tả tác dụng của quyền |
| `method` | `VARCHAR(10)` | Field | Phương thức HTTP (`GET`, `POST`, `PUT`, `DELETE`) |
| `endpoint` | `VARCHAR(255)` | Field | Đường dẫn API (`/students/**`) |
| `module` | `VARCHAR(50)` | Field | Phân hệ API (`STUDENT`, `TEACHER`, `COURSE`) |
| `is_public` | `BOOLEAN` | Field | Default `false` (Nếu `true` thì không cần đăng nhập) |

#### 5. Bảng `role_permission` (Bảng trung gian Gán Permission)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `role_code` | `VARCHAR(50)` | Logical FK | Mã vai trò $\rightarrow$ `role(role_code)` *(Index)* |
| `permission_code`| `VARCHAR(100)` | Logical FK | Mã quyền $\rightarrow$ `permission(permission_code)` *(Index)* |

---

### 4.2. Phân hệ Danh mục Đào tạo — Masterdata (6 Bảng)

#### 6. Bảng `department` (Khoa / Bộ môn)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `department_code` | `VARCHAR(20)` | **Code** | Mã khoa (`DEPT_CNTT`, `DEPT_DTVT`) *(Unique)* |
| `name` | `VARCHAR(100)` | **Name** | Tên khoa (`Khoa Công nghệ Thông tin`) |
| `description` | `TEXT` | Field | Mô tả thông tin khoa |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

#### 7. Bảng `major` (Ngành học)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `major_code` | `VARCHAR(20)` | **Code** | Mã ngành (`MJ_7480101`, `MJ_KTPM`) *(Unique)* |
| `name` | `VARCHAR(100)` | **Name** | Tên ngành (`Ngành Kỹ thuật Phần mềm`) |
| `department_id` | `VARCHAR(36)` | Logical FK | ID khoa trực thuộc $\rightarrow$ `department(id)` *(Index)* |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

#### 8. Bảng `subject` (Môn học khung)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `subject_code` | `VARCHAR(20)` | **Code** | Mã môn (`SUBJ_JAVA01`, `SUBJ_DATABASE`) *(Unique)* |
| `name` | `VARCHAR(100)` | **Name** | Tên môn (`Lập trình Java nâng cao`) |
| `credit` | `INT` | Field | Số tín chỉ môn học (`3`) |
| `description` | `TEXT` | Field | Đề cương chi tiết môn học |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

#### 9. Bảng `teacher` (Hồ sơ Giảng viên)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `teacher_code` | `VARCHAR(20)` | **Code** | Mã giảng viên (`GV001`, `GV002`) *(Unique)* |
| `name` / `full_name` | `VARCHAR(100)` | **Name** | Họ tên giảng viên (`TS. Nguyễn Văn B`) |
| `email` | `VARCHAR(100)` | Field | Email công vụ |
| `phone_number` | `VARCHAR(20)` | Field | Số điện thoại liên hệ |
| `specialization`| `VARCHAR(100)` | Field | Chuyên môn chuyên sâu |
| `degree` | `VARCHAR(50)` | Field | Học vị (`ThS`, `TS`, `GS`, `PGS`) |
| `department_id` | `VARCHAR(36)` | Logical FK | ID khoa công tác $\rightarrow$ `department(id)` *(Index)* |
| `user_id` | `VARCHAR(36)` | Logical FK | ID tài khoản đăng nhập $\rightarrow$ `user(id)` *(Index)* |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

#### 10. Bảng `class_group` (Lớp Hành chính / Lớp Sinh hoạt)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `class_code` | `VARCHAR(30)` | **Code** | Mã lớp (`CLGR_23CNTT01`) *(Unique)* |
| `name` / `class_name` | `VARCHAR(100)` | **Name** | Tên lớp (`Lớp Công nghệ thông tin K23-01`) |
| `major_id` | `VARCHAR(36)` | Logical FK | ID ngành học $\rightarrow$ `major(id)` *(Index)* |
| `academic_year` | `VARCHAR(20)` | Field | Niên khóa (`2023-2027`) |
| `homeroom_teacher_id` | `VARCHAR(36)` | Logical FK | ID giáo viên chủ nhiệm $\rightarrow$ `teacher(id)` *(Index)* |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

#### 11. Bảng `student` (Hồ sơ Sinh viên)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `student_code` | `VARCHAR(20)` | **Code** | Mã sinh viên (`SV2023001`) *(Unique)* |
| `name` / `full_name` | `VARCHAR(100)` | **Name** | Họ tên sinh viên (`Trần Thị C`) |
| `email` | `VARCHAR(100)` | Field | Email sinh viên |
| `phone_number` | `VARCHAR(20)` | Field | Số điện thoại cá nhân |
| `dob` | `DATE` | Field | Ngày tháng năm sinh |
| `gender` | `VARCHAR(10)` | Field | Giới tính (`MALE`, `FEMALE`, `OTHER`) |
| `address` | `VARCHAR(255)` | Field | Địa chỉ thường trú |
| `major_id` | `VARCHAR(36)` | Logical FK | ID ngành học $\rightarrow$ `major(id)` *(Index)* |
| `enrollment_year`| `VARCHAR(10)` | Field | Năm nhập học (`2023`) |
| `status` | `VARCHAR(20)` | Field | Trạng thái (`ACTIVE`, `SUSPENDED`, `GRADUATED`) |
| `class_group_id` | `VARCHAR(36)` | Logical FK | ID lớp sinh hoạt $\rightarrow$ `class_group(id)` *(Index)* |
| `user_id` | `VARCHAR(36)` | Logical FK | ID tài khoản đăng nhập $\rightarrow$ `user(id)` *(Index)* |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

---

### 4.3. Phân hệ Lớp học phần & Đăng ký & Điểm số (3 Bảng)

#### 12. Bảng `course_class` (Lớp học phần mở theo Học kỳ)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `course_class_code` | `VARCHAR(50)` | **Code** | Mã lớp HP (`CC_JAVA01_HK1_2526_N01`) *(Unique)* |
| `name` / `course_class_name` | `VARCHAR(100)` | **Name** | Tên lớp HP (`Lớp HP Lập trình Java - N01 - HK1`) |
| `subject_id` | `VARCHAR(36)` | Logical FK | ID môn học khung $\rightarrow$ `subject(id)` *(Index)* |
| `teacher_id` | `VARCHAR(36)` | Logical FK | ID giảng viên phụ trách $\rightarrow$ `teacher(id)` *(Index)* |
| `semester` | `VARCHAR(10)` | Field | Học kỳ (`HK1`, `HK2`, `HK3`) |
| `academic_year` | `VARCHAR(20)` | Field | Năm học (`2025-2026`) |
| `max_capacity` | `INT` | Field | Sĩ số tối đa (`60`) |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

#### 13. Bảng `enrollment` (Đăng ký Học phần & Bảng điểm)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `enrollment_code` | `VARCHAR(50)` | **Code** | Mã phiếu đăng ký (`ENR_20250901_0001`) *(Unique)* |
| `name` | `VARCHAR(100)` | **Name** | Mô tả đăng ký (`Đăng ký HP Java - SV2023001`) |
| `student_id` | `VARCHAR(36)` | Logical FK | ID sinh viên đăng ký $\rightarrow$ `student(id)` *(Index)* |
| `course_class_id`| `VARCHAR(36)` | Logical FK | ID lớp học phần $\rightarrow$ `course_class(id)` *(Index)* |
| `midterm_score` | `DOUBLE` | Field | Điểm quá trình / giữa kỳ (30%) |
| `final_score` | `DOUBLE` | Field | Điểm thi kết thúc HP (70%) |
| `total_score` | `DOUBLE` | Field | Điểm tổng kết hệ 10 |
| `status` | `VARCHAR(20)` | Field | `REGISTERED`, `ATTENDING`, `PASSED`, `FAILED`, `CANCELLED` |
| `enrolled_at` | `DATETIME` | Field | Thời điểm bấm nút đăng ký |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

#### 14. Bảng `class_schedule` (Thời khóa biểu)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(36)` | **PK** | UUID Primary Key |
| `schedule_code` | `VARCHAR(50)` | **Code** | Mã lịch học (`SCH_JAVA01_T2_P301`) *(Unique)* |
| `name` | `VARCHAR(100)` | **Name** | Tên ca học (`Lịch học Java - Thứ 2 Ca 1 - Phòng A2-301`) |
| `course_class_id`| `VARCHAR(36)` | Logical FK | ID lớp học phần $\rightarrow$ `course_class(id)` *(Index)* |
| `teacher_id` | `VARCHAR(36)` | Logical FK | ID giảng viên dạy $\rightarrow$ `teacher(id)` *(Index)* |
| `day_of_week` | `VARCHAR(15)` | Field | Thứ trong tuần (`MONDAY`, `TUESDAY`...) |
| `start_time` | `TIME` | Field | Giờ bắt đầu (`07:00:00`) |
| `end_time` | `TIME` | Field | Giờ kết thúc (`09:30:00`) |
| `room` | `VARCHAR(50)` | Field | Phòng học (`A2-301`) |
| `deleted` | `BOOLEAN` | Soft Delete | Default `false` |

---

### 4.4. Phân hệ Auth Token Blacklist (1 Bảng)

#### 15. Bảng `invalidated_token` (Blacklist JWT Logout)
| Cột | Kiểu dữ liệu | Quy chuẩn | Mô tả / Ví dụ |
|---|---|---|---|
| `id` | `VARCHAR(255)` | **PK** | JWT Token ID (`jit` claim) |
| `expiry_time` | `DATETIME` | Field | Thời điểm token hết hạn |

---

## ⚡ 5. Chiến lược Đánh Index Tối ưu trên MySQL

```sql
-- Thêm Index cho toàn bộ cột Khóa ngoại Logic để câu lệnh JOIN/WHERE đạt tốc độ cao nhất (O(log N)):
CREATE INDEX idx_user_role_user ON user_role(user_id);
CREATE INDEX idx_user_role_role_code ON user_role(role_code);

CREATE INDEX idx_role_perm_role_code ON role_permission(role_code);
CREATE INDEX idx_role_perm_perm_code ON role_permission(permission_code);

CREATE INDEX idx_major_department ON major(department_id);
CREATE INDEX idx_teacher_department ON teacher(department_id);
CREATE INDEX idx_teacher_user ON teacher(user_id);

CREATE INDEX idx_class_group_major ON class_group(major_id);
CREATE INDEX idx_class_group_teacher ON class_group(homeroom_teacher_id);

CREATE INDEX idx_student_class_group ON student(class_group_id);
CREATE INDEX idx_student_major ON student(major_id);
CREATE INDEX idx_student_user ON student(user_id);

CREATE INDEX idx_course_class_subject ON course_class(subject_id);
CREATE INDEX idx_course_class_teacher ON course_class(teacher_id);

CREATE INDEX idx_enrollment_student ON enrollment(student_id);
CREATE INDEX idx_enrollment_course_class ON enrollment(course_class_id);
CREATE UNIQUE INDEX uk_enrollment_student_class ON enrollment(student_id, course_class_id, deleted);

CREATE INDEX idx_schedule_course_class ON class_schedule(course_class_id);
CREATE INDEX idx_schedule_teacher ON class_schedule(teacher_id);
```

---

## 🛠️ 6. Quy tắc Xử lý Logic ở Tầng Java Service Layer

1. **Validation trước khi Lưu (Application-level Integrity Check)**:
   - Trước khi tạo/sửa `Student`, Service gọi `classGroupRepository.existsByIdAndDeletedFalse(classGroupId)`. Nếu `false`, ném `AppException(ErrorCode.CLASS_GROUP_NOT_FOUND)`.
2. **Gom ID Batch Fetching (Batch DTO Assembler)**:
   - Khi trả về danh sách phân trang `PageResponse<StudentResponse>`, Service lấy danh sách `classGroupId` trong trang đó $\rightarrow$ gọi `findAllByIdInAndDeletedFalse` đúng 1 lần $\rightarrow$ map vào DTO. Đảm bảo **chỉ tốn đúng 2 câu lệnh SQL** thay vì $N+1$ câu lệnh.

---

## 💻 7. Mẫu Code Java Entity & Service Assembler

### Java Entity (`Student.java`)
```java
package com.toan.university_management.entity.masterdata;

import com.toan.university_management.enums.StudentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "student", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_student_code_deleted", columnNames = {"student_code", "deleted"})
    },
    indexes = {
        @Index(name = "idx_student_class_group_id", columnList = "class_group_id"),
        @Index(name = "idx_student_major_id", columnList = "major_id"),
        @Index(name = "idx_student_user_id", columnList = "user_id")
    }
)
@SQLDelete(sql = "UPDATE student SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    String id;

    // --- QUY CHUẨN ĐỒNG BỘ: CODE & NAME ---
    @Column(name = "student_code", nullable = false)
    String studentCode; // Code: SV2023001

    @Column(name = "full_name", nullable = false)
    String fullName; // Name: Trần Thị C

    // --- CÁC THUỘC TÍNH NGHIỆP VỤ ---
    @Column(name = "email")
    String email;

    @Column(name = "phone_number")
    String phoneNumber;

    @Column(name = "dob")
    LocalDate dob;

    @Column(name = "gender")
    String gender;

    @Column(name = "address")
    String address;

    @Column(name = "enrollment_year")
    String enrollmentYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    StudentStatus status = StudentStatus.ACTIVE;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    boolean deleted = false;

    // --- LOGICAL FOREIGN KEYS ---
    @Column(name = "class_group_id")
    String classGroupId;

    @Column(name = "major_id")
    String majorId;

    @Column(name = "user_id")
    String userId;
}
```

---

## 🚀 8. Lộ trình Triển khai (Migration Roadmap)

1. **Bước 1**: Cập nhật lại 15 Java `@Entity` classes theo đúng file thiết kế này.
2. **Bước 2**: Cập nhật Repositories (thêm các hàm `existsByIdAndDeletedFalse`, `findAllByIdInAndDeletedFalse`).
3. **Bước 3**: Cập nhật Service Impl (Thêm logic validation và Batch DTO assembly).
4. **Bước 4**: Cập nhật DTOs & MapStruct Mappers.
5. **Bước 5**: Chạy ứng dụng $\rightarrow$ Kiểm tra DDL SQL của Hibernate để xác nhận không còn câu lệnh `ALTER TABLE ADD CONSTRAINT FOREIGN KEY` nào.
