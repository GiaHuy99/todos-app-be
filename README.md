# todos-app-be (Backend)

Spring Boot REST API for the Todo List application. Persists data in **AWS RDS MySQL**, runs in **Docker** on **AWS EC2**, and is consumed by the React frontend via a Cloudflare Worker proxy.

**Frontend repo:** [todos-app](../todos-app)

---

## Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.1 |
| Language | Java 21 |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8 (AWS RDS) |
| Build | Maven |
| Container | Docker (multi-stage, Eclipse Temurin 21) |

---

## Project structure

```
todos-app-be/
├── src/main/java/huypro/todoappbe/
│   ├── controller/       # REST endpoints
│   ├── service/          # Business logic
│   ├── repository/       # JPA repositories
│   ├── entity/           # JPA entities
│   ├── dto/              # Request / response models
│   ├── mapper/           # DTO ↔ entity mapping
│   ├── config/           # CORS, etc.
│   └── common/           # Exceptions, pagination, utilities
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── docs/
└── pom.xml
```

---

## Local development

Requires MySQL running locally with database `todo_app`.

```bash
# Set env vars (or use defaults in application.properties for local)
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/todo_app?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password

./mvnw spring-boot:run
```

API available at [http://localhost:8080/api/todos](http://localhost:8080/api/todos).

Pair with the frontend:

```bash
cd ../todos-app && npm run dev
```

---

## Configuration

Database credentials are injected via environment variables (required for Docker/production):

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL (RDS endpoint in production) |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |

Example RDS URL:

```
jdbc:mysql://database-1.xxxxx.us-east-1.rds.amazonaws.com:3306/todo_app?createDatabaseIfNotExist=true&useSSL=true&requireSSL=true&serverTimezone=UTC
```

---

## Docker

### Build (linux/amd64 for EC2)

```bash
docker buildx build \
  --platform linux/amd64 \
  -t huy1412/todo-app-be:latest \
  --load \
  .
```

### Push to Docker Hub

```bash
docker push huy1412/todo-app-be:latest
```

### Run (production example)

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

## AWS deployment

```
Cloudflare Worker (frontend)
        │  HTTP /api/**
        ▼
EC2 (Docker :8080)  ──►  RDS MySQL (:3306)
```

### Checklist

| Step | Detail |
|---|---|
| RDS | MySQL instance in same VPC as EC2 |
| RDS Security Group | Inbound 3306 from **EC2 Security Group** |
| EC2 Security Group | Inbound **8080** from `0.0.0.0/0` (Worker + public API) |
| Database | Create `todo_app` or use `createDatabaseIfNotExist=true` in JDBC URL |
| Docker | Pull image, run with `SPRING_DATASOURCE_*` env vars |
| Frontend | Set `BACKEND_URL` in `todos-app/wrangler.json` to EC2 public DNS/IP |

Example `BACKEND_URL`:

```
http://ec2-3-84-174-239.compute-1.amazonaws.com:8080
```

---

## API overview

| Method | Path | Description |
|---|---|---|
| GET | `/api/todos` | List (paginated, searchable, filterable) |
| POST | `/api/todos` | Create |
| GET | `/api/todos/{id}` | Get by ID |
| PUT | `/api/todos/{id}` | Full update |
| PATCH | `/api/todos/{id}` | Partial update (e.g. toggle completed) |
| DELETE | `/api/todos/{id}` | Delete |

Full specification: [docs/Technical-Design.md](./docs/Technical-Design.md)

---

## Documentation

| Document | Description |
|---|---|
| [docs/PRD.md](./docs/PRD.md) | Backend scope & API requirements |
| [docs/Technical-Design.md](./docs/Technical-Design.md) | Architecture, API contract, deployment |
| [todos-app/docs/PRD.md](../todos-app/docs/PRD.md) | Full product requirements (UI) |
| [todos-app/docs/DESIGN.md](../todos-app/docs/DESIGN.md) | UI design system |

---

## Scripts

| Command | Description |
|---|---|
| `./mvnw spring-boot:run` | Run locally |
| `./mvnw test` | Run tests |
| `./mvnw package -DskipTests` | Build JAR |
| `docker build ...` | Build container image |
