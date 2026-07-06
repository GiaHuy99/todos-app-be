# todos-app-be (Backend)

Spring Boot REST API for the Todo List application — layered architecture, JPA persistence, validation, and Docker support.

**Frontend repo:** [todos-app](../todos-app)

---

## Technologies

| Category | Technologies |
|---|---|
| **Framework** | Spring Boot 4.1, Spring Web MVC |
| **Language** | Java 21 |
| **Persistence** | Spring Data JPA, Hibernate, MySQL 8 |
| **Validation** | Jakarta Bean Validation |
| **Utilities** | Lombok |
| **Build** | Maven |
| **Container** | Docker (multi-stage, Eclipse Temurin 21) |

---

## Features

- REST API for todo CRUD with pagination, search, and filter
- Request validation (`@Valid`, `@NotBlank`, `@Size`)
- Global exception handling with consistent JSON errors
- CORS configuration for frontend origins
- Environment-based database configuration (no hardcoded credentials)
- Dockerized for portable deployment

---

## Project structure

```
todos-app-be/
├── src/main/java/huypro/todoappbe/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── mapper/
│   ├── config/
│   └── common/
├── src/main/resources/application.properties
├── Dockerfile
├── docs/
└── pom.xml
```

---

## Local development

Requires MySQL with database `todo_app`.

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/todo_app?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password

./mvnw spring-boot:run
```

API: [http://localhost:8080/api/todos](http://localhost:8080/api/todos)

Pair with frontend: `cd ../todos-app && npm run dev`

---

## Configuration

Database settings via environment variables:

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |

---

## Docker

```bash
# Build
docker build -t todo-app-be:latest .

# Run (set your own env values)
docker run -d \
  --name todo-app-be \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://YOUR_DB_HOST:3306/todo_app?useSSL=true&serverTimezone=UTC" \
  -e SPRING_DATASOURCE_USERNAME=your_username \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  todo-app-be:latest
```

---

## API overview

| Method | Path | Description |
|---|---|---|
| GET | `/api/todos` | List (paginated, searchable, filterable) |
| POST | `/api/todos` | Create |
| GET | `/api/todos/{id}` | Get by ID |
| PUT | `/api/todos/{id}` | Full update |
| PATCH | `/api/todos/{id}` | Partial update |
| DELETE | `/api/todos/{id}` | Delete |

Full specification: [docs/Technical-Design.md](./docs/Technical-Design.md)

---

## Scripts

| Command | Description |
|---|---|
| `./mvnw spring-boot:run` | Run locally |
| `./mvnw test` | Run tests |
| `./mvnw package -DskipTests` | Build JAR |
| `docker build -t todo-app-be .` | Build image |

---

## Documentation

| Document | Description |
|---|---|
| [docs/PRD.md](./docs/PRD.md) | Backend requirements |
| [docs/Technical-Design.md](./docs/Technical-Design.md) | Architecture & API contract |
| [todos-app/docs/PRD.md](../todos-app/docs/PRD.md) | Product requirements (UI) |
| [todos-app/docs/DESIGN.md](../todos-app/docs/DESIGN.md) | UI design system |
