# AGENTS.md — University Management System

> **Mục đích**: File này là điểm đọc đầu tiên cho AI agent khi bắt đầu phiên làm việc mới.
> Đọc file này TRƯỚC, sau đó đọc `docs/CONTEXT.md` để biết trạng thái hiện tại.

---

## Tổng quan dự án

Hệ thống quản lý đại học (University Management System) — backend REST API xây dựng trên Spring Boot.
Quản lý sinh viên, giảng viên, môn học, phân quyền động theo API endpoint.

## Tech Stack

| Thành phần       | Công nghệ                                      |
| ---------------- | ----------------------------------------------- |
| Ngôn ngữ         | Java 21                                         |
| Framework        | Spring Boot 3.5.5                               |
| Build tool       | Maven                                           |
| Database         | MySQL (dev/prod), H2 (test)                     |
| Cache            | Redis                                           |
| ORM              | Spring Data JPA + Hibernate                     |
| Security         | Spring Security + OAuth2 Resource Server (JWT)  |
| Mapping DTO      | MapStruct 1.5.5                                 |
| Report           | JasperReports 6.20.6                            |
| Test             | JUnit 5, Testcontainers (MySQL)                 |
| Code generation  | Lombok                                          |
| Coverage         | Jacoco                                          |

## Cấu trúc thư mục chính

```
university-management/
├── pom.xml
├── src/main/java/com/toan/university_management/
│   ├── configuration/       # Security, Redis, JWT, CORS, Auto-scanner
│   ├── controller/          # REST controllers (7 controllers)
│   ├── dto/
│   │   ├── request/         # Request DTOs
│   │   └── response/        # Response DTOs (bao gồm ApiResponse envelope)
│   ├── entity/              # JPA entities (7 entities)
│   ├── enums/               # Enum definitions
│   ├── exception/           # AppException, ErrorCode, GlobalExceptionHandler
│   ├── mapper/              # MapStruct interfaces
│   ├── repository/          # Spring Data JPA repositories
│   ├── reportmock/          # JasperReports mock data
│   └── service/
│       ├── *.java           # Service interfaces
│       └── implement/       # Service implementations
├── src/main/resources/
│   ├── application.yml      # Cấu hình chính
│   ├── reports/             # JasperReport templates
│   ├── static/
│   └── templates/
├── src/test/
└── docs/
    └── architecture/        # Tài liệu kiến trúc
```

## Entities & Quan hệ

| Entity            | Quan hệ chính                                           |
| ----------------- | -------------------------------------------------------- |
| `User`            | `@ManyToMany` → `Role`                                  |
| `Role`            | `@ManyToMany` → `Permission`, `@ManyToMany` ← `User`   |
| `Permission`      | method + endpoint + module (phân quyền động theo API)    |
| `Student`         | `@ManyToMany` → `Course`                                |
| `Teacher`         | `@OneToMany` → `Course`                                 |
| `Course`          | `@ManyToOne` → `Teacher`, `@ManyToMany` → `Student`     |
| `InvalidatedToken`| JWT blacklist cho logout                                 |

## API Endpoints tổng quan

| Controller                  | Base Path        | Chức năng                        |
| --------------------------- | ---------------- | -------------------------------- |
| `AuthenticationController`  | `/auth`          | Login, logout, refresh token     |
| `StudentController`         | `/students`      | CRUD sinh viên                   |
| `TeacherController`         | `/teachers`      | CRUD giảng viên                  |
| `CourseController`          | `/courses`       | CRUD môn học, tìm theo giảng viên|
| `UserController`            | `/users`         | CRUD tài khoản                   |
| `RoleController`            | `/roles`         | Quản lý vai trò                  |
| `PermissionController`      | `/permissions`   | Quản lý quyền                   |

## Conventions

- **Ngôn ngữ tài liệu**: Tiếng Việt
- **API Response**: Luôn bọc trong `ApiResponse` envelope (xem rule P3)
- **Coding style**: Xem `.gemini/rules/core/coding-style.md`
- **Workflow**: Xem `.gemini/rules/core/workflow.md`
- **File naming**: kebab-case cho file tài liệu, PascalCase cho Java class
- **Commit**: Conventional commits có scope — `type(scope): mô tả`
- **Branch**: Không tự tạo branch; khi được yêu cầu luôn đi kèm worktree

## Cấu hình môi trường

```yaml
# Biến môi trường cần thiết
DB_URL:       jdbc:mysql://localhost:3306/university  # MySQL connection
DB_USERNAME:  (required)
DB_PASSWORD:  (required)
REDIS_HOST:   localhost                                # Redis host
REDIS_PORT:   6379                                     # Redis port
SIGNER_KEY:   (required)                               # JWT signing key
```

## Lệnh thường dùng

```bash
# Chạy ứng dụng
./mvnw spring-boot:run

# Chạy test
./mvnw test

# Build
./mvnw clean package -DskipTests

# Chạy với profile cụ thể
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Tài liệu liên quan

- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — Kiến trúc tổng thể
- [`docs/CONTEXT.md`](docs/CONTEXT.md) — Ngữ cảnh hiện tại, issues, plan
- [`docs/architecture/security-api-permission-design.md`](docs/architecture/security-api-permission-design.md) — Thiết kế phân quyền API

## Chưa khớp thực tế

| Claim | Ý định | Trạng thái | Bằng chứng |
| ----- | ------ | ---------- | ----------- |
| (rỗng — cập nhật khi phát hiện sai lệch) | | | |
