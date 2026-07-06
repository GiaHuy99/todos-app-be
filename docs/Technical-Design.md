# Technical Design — Backend

| Field | Value |
|---|---|
| **Repository** | `todos-app-be` |
| **Version** | 1.2 |
| **Status** | Complete |
| **Related** | [PRD](./PRD.md) · [Frontend Technical Design](../todos-app/docs/Technical-Design.md) |

---

## 1. Overview

Spring Boot REST API with layered architecture, JPA persistence, and Docker packaging.

```
Client → Controller → Service → Mapper → Repository → MySQL
```

---

## 2. Technologies

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.1 |
| Language | Java 21 |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8 |
| Validation | Jakarta Bean Validation |
| Build | Maven |
| Container | Docker (Eclipse Temurin 21) |

---

## 3. Package structure

```
huypro.todoappbe/
├── controller/       TodoController
├── service/          TodoService, TodoServiceImpl
├── repository/       TodoRepository
├── entity/           TodoEntity
├── dto/              Request & response records
├── mapper/           TodoMapper, TodoMapperImpl
├── config/           CorsConfig
└── common/           Exceptions, pagination, TextNormalizer
```

---

## 4. Database schema

**Table: `todos`**

| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | PK, auto-increment |
| title | VARCHAR(120) | NOT NULL |
| description | VARCHAR(500) | NULL |
| completed | BOOLEAN | NOT NULL, default false |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | NOT NULL |

---

## 5. Configuration

```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
```

Credentials are always injected via environment variables.

---

## 6. REST API specification

**Base path:** `/api` · **Content-Type:** `application/json`

### Endpoints

| Method | Path | Status |
|---|---|---|
| GET | `/api/todos` | 200 |
| POST | `/api/todos` | 201 |
| GET | `/api/todos/{id}` | 200 / 404 |
| PUT | `/api/todos/{id}` | 200 / 404 |
| PATCH | `/api/todos/{id}` | 200 / 404 |
| DELETE | `/api/todos/{id}` | 204 / 404 |

### List query parameters

| Param | Default | Description |
|---|---|---|
| `page` | 0 | Page index |
| `size` | 10 | Page size |
| `completed` | — | Filter by status |
| `search` | — | Title search (case-insensitive) |

### Validation (request body)

| DTO | title | description |
|---|---|---|
| CreateTodoRequest | `@NotBlank`, `@Size(max=120)` | `@Size(max=500)` |
| UpdateTodoRequest | `@NotBlank`, `@Size(max=120)` | `@Size(max=500)` |
| PatchTodoRequest | `@Size(max=120)` | `@Size(max=500)` |

PATCH also requires at least one field; empty title after trim returns 400 via `TextNormalizer`.

### Error format

```json
{ "message": "Title is required.", "status": 400 }
```

---

## 7. Error handling

| Exception | HTTP |
|---|---|
| `MethodArgumentNotValidException` | 400 |
| `IllegalArgumentException` | 400 |
| `TodoNotFoundException` | 404 |
| Other | 500 |

---

## 8. CORS

`CorsConfig` allows configured frontend origins. When the frontend uses an edge proxy (same-origin `/api`), browser CORS is not required for normal usage.

---

## 9. Docker

Multi-stage `Dockerfile`: JDK build stage → JRE Alpine run stage, non-root user, port 8080.

```bash
docker build -t todo-app-be:latest .
docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=... -e SPRING_DATASOURCE_USERNAME=... -e SPRING_DATASOURCE_PASSWORD=... todo-app-be:latest
```

---

## 10. Implementation status

| Component | Status |
|---|---|
| REST API | ✅ |
| MySQL persistence | ✅ |
| Validation | ✅ |
| Docker image | ✅ |
| Integration tests | ⬜ |

---

## 11. References

- [PRD](./PRD.md)
- [Frontend Technical Design](../todos-app/docs/Technical-Design.md)
