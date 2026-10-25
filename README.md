# Task Tracker API

Task/Job Tracker REST API built with Spring Boot 3, Spring Security (JWT), Spring Data JPA, PostgreSQL and Flyway.

## Features
- JWT authentication (register / login) with BCrypt password hashing
- Role-based authorization (`USER`, `ADMIN`)
- Task CRUD with ownership: users can only see and modify their own tasks
- Bean validation and a global exception handler with consistent JSON errors
- Flyway migrations, Docker / docker-compose, GitHub Actions CI

## Run
Requires Java 17 and Maven.

```bash
docker compose up -d db      # PostgreSQL only
mvn spring-boot:run
```
Or run everything in containers: `docker compose up --build`.

Config via env vars: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (min 32 chars), `JWT_EXPIRATION_MS`.

## Test
```bash
mvn verify
```
Tests use an in-memory H2 database (PostgreSQL mode) with the same Flyway migrations.

## API overview
| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Register, returns JWT |
| POST | `/api/auth/login` | Login, returns JWT |
| GET | `/api/tasks` | List own tasks |
| POST | `/api/tasks` | Create task |
| GET | `/api/tasks/{id}` | Get own task |
| PUT | `/api/tasks/{id}` | Update own task |
| DELETE | `/api/tasks/{id}` | Delete own task |
| GET | `/api/tasks/admin/all` | List all tasks (ADMIN only) |

Send the returned token as an HTTP ****** (`Authorization` header, value `****** on `/api/tasks/**`.

```bash
curl -X POST localhost:8080/api/auth/register -H 'Content-Type: application/json' \
  -d '{"username":"alice","email":"alice@example.com","password":"password123"}'
```
Task statuses: `TODO`, `IN_PROGRESS`, `DONE`. New users get the `USER` role; promote to `ADMIN` by updating `users.role` in the database.
