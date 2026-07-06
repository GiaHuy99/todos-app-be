# Product Requirements — Backend

| Field | Value |
|---|---|
| **Repository** | `todos-app-be` |
| **Version** | 1.2 |
| **Status** | Complete |
| **Full product PRD** | [todos-app/docs/PRD.md](../todos-app/docs/PRD.md) |

Backend-specific scope. UI requirements live in the frontend repository.

---

## 1. Backend responsibilities

- REST API for todo CRUD
- Server-side pagination, search, and filter
- Input validation and consistent error responses
- JPA persistence in MySQL
- Docker packaging for portable deployment

---

## 2. Technologies

Spring Boot 4.1 · Java 21 · Spring Data JPA · MySQL 8 · Jakarta Validation · Lombok · Maven · Docker

---

## 3. API requirements

| ID | Requirement |
|---|---|
| API-01 | `GET /api/todos` with pagination (`page`, `size`) |
| API-02 | Filter by `completed` query param |
| API-03 | Search by `search` query param (title, case-insensitive) |
| API-04 | `POST /api/todos` → 201 |
| API-05 | `GET /api/todos/{id}` |
| API-06 | `PUT /api/todos/{id}` full update |
| API-07 | `PATCH /api/todos/{id}` partial update |
| API-08 | `DELETE /api/todos/{id}` → 204 |
| API-09 | 404 when todo not found |
| API-10 | 400 on validation failure |

---

## 4. Data model

| Field | Type | Constraints |
|---|---|---|
| id | BIGINT | Auto-generated |
| title | string | Required, max 120 |
| description | string | Optional, max 500 |
| completed | boolean | Default false |
| createdAt | timestamp | Auto |
| updatedAt | timestamp | Auto |

---

## 5. Validation rules

| Field | Rules |
|---|---|
| title | `@NotBlank`, `@Size(max = 120)` |
| description | `@Size(max = 500)` |

---

## 6. Integration

| Environment | Connection |
|---|---|
| Local dev | Direct `http://localhost:8080/api` or via Vite proxy |
| Production | Frontend edge proxy forwards `/api/**` to backend |

---

## 7. Out of scope (MVP)

- Authentication / authorization
- Multi-tenant isolation
- Database migrations (Flyway / Liquibase)
- Rate limiting

---

## 8. References

- [Technical Design](./Technical-Design.md)
- [Frontend PRD](../todos-app/docs/PRD.md)
- [Frontend Technical Design](../todos-app/docs/Technical-Design.md)
