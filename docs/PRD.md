# Product Requirements — Backend

| Field | Value |
|---|---|
| **Repository** | `todos-app-be` |
| **Version** | 1.2 |
| **Status** | Deployed |
| **Full product PRD** | [todos-app/docs/PRD.md](../todos-app/docs/PRD.md) |

This document covers **backend-specific** scope. UI requirements, user stories, and design live in the frontend repository.

---

## 1. Backend responsibilities

- Expose a REST API for todo CRUD operations
- Persist todos in MySQL with pagination, search, and filter support
- Validate input on the server
- Return consistent error responses
- Run in Docker on AWS EC2, connected to AWS RDS

---

## 2. API requirements

| ID | Requirement |
|---|---|
| API-01 | Provide `GET /api/todos` with pagination (`page`, `size`) |
| API-02 | Support `completed` query param for status filtering |
| API-03 | Support `search` query param for case-insensitive title search |
| API-04 | Provide `POST /api/todos` to create a todo (201) |
| API-05 | Provide `GET /api/todos/{id}` to fetch a single todo |
| API-06 | Provide `PUT /api/todos/{id}` for full update |
| API-07 | Provide `PATCH /api/todos/{id}` for partial update (toggle completed) |
| API-08 | Provide `DELETE /api/todos/{id}` (204 on success) |
| API-09 | Return 404 when todo ID does not exist |
| API-10 | Return 400 with message on validation failure |

---

## 3. Data model

| Field | Type | Required | Constraints |
|---|---|---|---|
| id | BIGINT | Yes | Auto-generated |
| title | VARCHAR(120) | Yes | 1–120 characters |
| description | VARCHAR(500) | No | Max 500 characters |
| completed | BOOLEAN | Yes | Default `false` |
| createdAt | TIMESTAMP | Yes | Set on create |
| updatedAt | TIMESTAMP | Yes | Set on create/update |

---

## 4. Validation rules

| Field | Server rules |
|---|---|
| title | `@NotBlank`, `@Size(max = 120)` |
| description | `@Size(max = 500)` |
| completed | Boolean (PATCH/PUT) |

---

## 5. Infrastructure requirements

| ID | Requirement |
|---|---|
| INF-01 | Run as Docker container on AWS EC2 (port 8080) |
| INF-02 | Connect to AWS RDS MySQL in the same VPC |
| INF-03 | Database credentials via environment variables (`SPRING_DATASOURCE_*`) |
| INF-04 | EC2 Security Group allows inbound 8080 for Cloudflare Worker |
| INF-05 | RDS Security Group allows inbound 3306 from EC2 only |
| INF-06 | Docker image published to Docker Hub, pulled on EC2 |

---

## 6. Integration

| Environment | How frontend reaches backend |
|---|---|
| Local dev | Vite proxy: `/api` → `http://localhost:8080` |
| Production | Cloudflare Worker proxy: `/api/**` → `BACKEND_URL` (EC2) |

The backend does not need to handle browser CORS in production when accessed through the Worker proxy. `CorsConfig` is configured for the Workers domain as a fallback.

---

## 7. Out of scope (MVP)

- Authentication / authorization
- Multi-tenant data isolation
- Database migrations (Flyway / Liquibase)
- HTTPS termination on EC2 (HTTP behind Worker proxy)
- Rate limiting
- Audit logging

---

## 8. References

- [Technical Design](./Technical-Design.md) — Architecture, API contract, Docker, AWS
- [Frontend PRD](../todos-app/docs/PRD.md) — Full product requirements
- [Frontend Technical Design](../todos-app/docs/Technical-Design.md) — Worker proxy, Cloudflare deploy
