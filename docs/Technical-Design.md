# Technical Design — Backend

| Field | Value |
|---|---|
| **Repository** | `todos-app-be` |
| **Version** | 1.2 |
| **Status** | Deployed |
| **Related** | [PRD](./PRD.md) · [Frontend Technical Design](../todos-app/docs/Technical-Design.md) |

---

## 1. Overview

Spring Boot REST API providing CRUD operations for todos with server-side pagination, search, and filtering. Deployed as a Docker container on AWS EC2, connected to AWS RDS MySQL.

```
Cloudflare Worker (frontend proxy)
        │  HTTP /api/**
        ▼
┌─────────────────────────────────────────┐
│  EC2 — Docker (todo-app-be :8080)       │
│  Controller → Service → Repository      │
└──────────────────┬──────────────────────┘
                   │ JDBC (private VPC)
                   ▼
┌─────────────────────────────────────────┐
│  RDS MySQL — database: todo_app         │
└─────────────────────────────────────────┘
```

---

## 2. Technology stack

| Layer | Technology | Version |
|---|---|---|
| Framework | Spring Boot | 4.1.x |
| Language | Java | 21 |
| ORM | Spring Data JPA / Hibernate | 7.x |
| Database | MySQL | 8.x (AWS RDS) |
| Validation | Jakarta Validation | — |
| Utilities | Lombok | — |
| Build | Maven | 3.x |
| Runtime | Docker (Eclipse Temurin 21 JRE Alpine) | — |

---

## 3. Repository structure

```
src/main/java/huypro/todoappbe/
├── TodoAppBeApplication.java
├── config/
│   └── CorsConfig.java
├── controller/
│   └── TodoController.java
├── service/
│   ├── TodoService.java
│   └── TodoServiceImpl.java
├── repository/
│   └── TodoRepository.java
├── entity/
│   └── TodoEntity.java
├── dto/
│   ├── request/          # CreateTodoRequest, UpdateTodoRequest, PatchTodoRequest
│   └── response/         # TodoResponse
├── mapper/
│   ├── TodoMapper.java
│   └── TodoMapperImpl.java
└── common/
    ├── dto/ErrorResponse.java
    ├── exception/        # GlobalExceptionHandler, TodoNotFoundException
    ├── pagination/       # PageResponse
    └── util/             # TextNormalizer
```

---

## 4. Layered architecture

```
TodoController (REST)
    │
    ▼
TodoService / TodoServiceImpl (business logic)
    │
    ▼
TodoMapper (DTO ↔ entity)
    │
    ▼
TodoRepository (JPA)
    │
    ▼
MySQL (todos table)
```

### SOLID conventions

| Principle | Implementation |
|---|---|
| Single Responsibility | `TextNormalizer`, `TodoMapper`, service layer |
| Open/Closed | `TodoService`, `TodoMapper` interfaces |
| Dependency Inversion | Controller → `TodoService` interface via Spring DI |

---

## 5. Database schema

**Table: `todos`**

| Column | SQL Type | Constraints |
|---|---|---|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| title | VARCHAR(120) | NOT NULL |
| description | VARCHAR(500) | NULL |
| completed | BOOLEAN | NOT NULL, DEFAULT FALSE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

Schema is managed by Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

---

## 6. Application configuration

```properties
# src/main/resources/application.properties
spring.application.name=todo-app-be
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
```

All database credentials are injected via environment variables — never hardcoded in the image.

### Local development

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/todo_app?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password

./mvnw spring-boot:run
```

### Production (Docker on EC2)

```bash
docker run -d \
  --name todo-app-be \
  --restart unless-stopped \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://<RDS_ENDPOINT>:3306/todo_app?createDatabaseIfNotExist=true&useSSL=true&requireSSL=true&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME=admin \
  -e SPRING_DATASOURCE_PASSWORD=<password> \
  huy1412/todo-app-be:latest
```

---

## 7. REST API specification

**Base path:** `/api`  
**Content-Type:** `application/json`

### 7.1 Endpoints

| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/api/todos` | List todos (paginated, filterable) | 200 |
| POST | `/api/todos` | Create a todo | 201 |
| GET | `/api/todos/{id}` | Get todo by ID | 200 / 404 |
| PUT | `/api/todos/{id}` | Full update | 200 / 404 |
| PATCH | `/api/todos/{id}` | Partial update (e.g. completed) | 200 / 404 |
| DELETE | `/api/todos/{id}` | Delete todo | 204 / 404 |

### 7.2 List todos — query parameters

| Param | Type | Default | Description |
|---|---|---|---|
| `page` | int | `0` | Zero-based page index |
| `size` | int | `10` | Page size |
| `completed` | boolean | — | Filter by completion status |
| `search` | string | — | Case-insensitive title search |

**Response (`PageResponse<TodoResponse>`):**

```json
{
  "content": [
    {
      "id": 1,
      "title": "Build REST API endpoints",
      "description": "Implement CRUD for todos.",
      "completed": false,
      "createdAt": "2026-07-01T08:00:00.000Z",
      "updatedAt": "2026-07-01T08:00:00.000Z"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 42,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

### 7.3 Request bodies

**Create (POST):**

```json
{ "title": "New task", "description": "Optional details" }
```

**Update (PUT):**

```json
{ "title": "Updated title", "description": "Updated description", "completed": true }
```

**Patch (PATCH):**

```json
{ "completed": true }
```

### 7.4 Error responses

```json
{ "message": "Title is required.", "status": 400 }
```

| HTTP Status | Scenario |
|---|---|
| 400 | Validation failure |
| 404 | Todo not found |
| 500 | Internal server error |

---

## 8. CORS

`CorsConfig` allows the Cloudflare Workers frontend origin:

```java
.allowedOrigins("https://todos-app.huykidbestboy1412.workers.dev")
```

In production the browser calls the Worker (same origin), so CORS is not required for normal usage. CORS is configured for direct API access during debugging.

---

## 9. Error handling

| Layer | Strategy |
|---|---|
| Validation | `@Valid` on request DTOs (`@NotBlank`, `@Size`) |
| Not found | `TodoNotFoundException` → 404 |
| Global | `@RestControllerAdvice` → consistent `ErrorResponse` JSON |

---

## 10. Docker

Multi-stage build (`Dockerfile`):

1. **Build stage:** `eclipse-temurin:21-jdk-alpine` + `./mvnw package`
2. **Run stage:** `eclipse-temurin:21-jre-alpine`, non-root `spring` user, port 8080

```bash
# Build for EC2 (amd64)
docker buildx build --platform linux/amd64 -t huy1412/todo-app-be:latest --load .

# Push
docker push huy1412/todo-app-be:latest
```

---

## 11. AWS infrastructure

| Component | Detail |
|---|---|
| **EC2** | Ubuntu, Docker installed, port 8080 exposed |
| **RDS** | MySQL 8, database `todo_app`, same VPC as EC2 |
| **EC2 Security Group** | Inbound TCP 8080 from `0.0.0.0/0`, SSH 22 from your IP |
| **RDS Security Group** | Inbound TCP 3306 from EC2 Security Group |
| **Image registry** | Docker Hub (`huy1412/todo-app-be`) |

### Connectivity checklist

1. EC2 can reach RDS: `docker logs` shows `Started TodoAppBeApplication`
2. Internet can reach EC2: `curl http://<EC2_PUBLIC_IP>:8080/api/todos`
3. Cloudflare Worker can reach EC2: `curl https://<workers.dev>/api/todos`

---

## 12. Security (MVP)

- No authentication
- Input validation on server (`@Valid` DTOs)
- SQL injection prevented via JPA parameterized queries
- Database credentials via environment variables only
- RDS not exposed to public internet (EC2 SG only)

---

## 13. Implementation status

| Component | Status |
|---|---|
| REST API | ✅ Complete |
| MySQL persistence (RDS) | ✅ Complete |
| Validation | ✅ Complete |
| Docker image | ✅ Complete |
| EC2 deployment | ✅ Complete |
| Integration tests | ⬜ Not implemented |
| Database migrations (Flyway) | ⬜ Not implemented |

---

## 14. References

- [PRD](./PRD.md) — Backend requirements
- [Frontend Technical Design](../todos-app/docs/Technical-Design.md) — React, Worker proxy, Cloudflare deploy
- [Frontend PRD](../todos-app/docs/PRD.md) — Full product requirements
