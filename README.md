# University Management System

Hệ thống quản lý đại học — Backend REST API.

## Yêu cầu hệ thống

| Yêu cầu     | Phiên bản      |
| ------------ | -------------- |
| Java         | 21+            |
| MySQL        | 8.0+           |
| Redis        | 7.0+           |
| Maven        | 3.9+ (hoặc dùng `mvnw` đi kèm) |

## Cài đặt & Chạy

### 1. Clone repository

```bash
git clone <repo-url>
cd university-management
```

### 2. Cấu hình Database

Tạo database MySQL:

```sql
CREATE DATABASE university CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Cấu hình biến môi trường

Tạo file `.env` hoặc set biến môi trường:

```bash
DB_URL=jdbc:mysql://localhost:3306/university
DB_USERNAME=root
DB_PASSWORD=your_password
REDIS_HOST=localhost
REDIS_PORT=6379
SIGNER_KEY=your_jwt_secret_key_here
```

### 4. Chạy ứng dụng

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

Ứng dụng sẽ chạy tại `http://localhost:8080`.

### 5. Chạy test

```bash
./mvnw test
```

## API Endpoints

| Method | Endpoint               | Mô tả                    | Auth |
| ------ | ---------------------- | ------------------------- | ---- |
| POST   | `/auth/login`          | Đăng nhập, nhận JWT token | ❌    |
| POST   | `/auth/logout`         | Đăng xuất (blacklist token)| ✅   |
| POST   | `/auth/refresh`        | Làm mới token             | ✅    |
| GET    | `/students`            | Danh sách sinh viên       | ✅    |
| POST   | `/students`            | Tạo sinh viên mới         | ✅    |
| GET    | `/students/{id}`       | Chi tiết sinh viên        | ✅    |
| PUT    | `/students/{id}`       | Cập nhật sinh viên        | ✅    |
| DELETE | `/students/{id}`       | Xoá sinh viên             | ✅    |
| GET    | `/teachers`            | Danh sách giảng viên      | ✅    |
| POST   | `/teachers`            | Tạo giảng viên mới        | ✅    |
| GET    | `/teachers/{id}`       | Chi tiết giảng viên       | ✅    |
| PUT    | `/teachers/{id}`       | Cập nhật giảng viên       | ✅    |
| DELETE | `/teachers/{id}`       | Xoá giảng viên            | ✅    |
| GET    | `/courses`             | Danh sách môn học         | ✅    |
| POST   | `/courses`             | Tạo môn học mới           | ✅    |
| GET    | `/courses/{id}`        | Chi tiết môn học          | ✅    |
| PUT    | `/courses/{id}`        | Cập nhật môn học          | ✅    |
| DELETE | `/courses/{id}`        | Xoá môn học               | ✅    |
| GET    | `/courses/byteacher`   | Môn học theo giảng viên   | ✅    |
| GET    | `/users`               | Danh sách tài khoản       | ✅    |
| POST   | `/users`               | Tạo tài khoản mới        | ✅    |
| GET    | `/roles`               | Danh sách vai trò         | ✅    |
| GET    | `/permissions`         | Danh sách quyền           | ✅    |

> **Ghi chú**: ✅ Auth = cần JWT Bearer Token trong header `Authorization`.

## Cấu trúc dự án

```
src/main/java/com/toan/university_management/
├── configuration/    # Cấu hình Security, Redis, JWT, CORS
├── controller/       # REST API controllers
├── dto/              # Data Transfer Objects (request/response)
├── entity/           # JPA entities
├── enums/            # Enum definitions
├── exception/        # Exception handling
├── mapper/           # MapStruct DTO mappers
├── repository/       # Data access layer
├── reportmock/       # JasperReports mock data
└── service/          # Business logic layer
    └── implement/    # Service implementations
```

## Tài liệu

- [`AGENTS.md`](AGENTS.md) — Tổng quan cho AI agents
- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — Kiến trúc hệ thống
- [`docs/CONTEXT.md`](docs/CONTEXT.md) — Trạng thái hiện tại dự án
- [`docs/architecture/security-api-permission-design.md`](docs/architecture/security-api-permission-design.md) — Thiết kế phân quyền API

## License

Private project.
