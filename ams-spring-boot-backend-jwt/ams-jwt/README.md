# Attendance Management System (Spring Boot + JWT)

MVP backend with Clock In/Out, auto-invalidate, approver endpoints, and JWT auth.

## Run (H2, local)

```bash
mvn spring-boot:run
# API at http://localhost:8080
```

### Login (JWT)
```http
POST /auth/login
Content-Type: application/json

{ "username": "approver@ams.local", "password": "demo" }
```
Response:
```json
{ "accessToken": "<jwt>", "expiresAt": "2025-01-01T00:00:00Z", "user": { ... } }
```
Use header `Authorization: Bearer <jwt>` for all protected endpoints.

### Endpoints (sample)
- POST `/attendance/clock-in` (multipart: lat?, lng?, selfie?)
- POST `/attendance/clock-out` (multipart: lat?, lng?, selfie?)
- GET  `/attendance/me?date=YYYY-MM-DD`
- GET  `/admin/attendance?employeeId=&date=`
- GET  `/admin/attendance/summary?employeeId=&from=&to=`
- POST `/admin/attendance/auto-invalidate`
- POST `/attendance/corrections`
- PATCH `/attendance/corrections/{id}/decision`

## Config
- JWT secret & expiry: `src/main/resources/application.yml` (`jwt.secret`, `jwt.exp-minutes`)
- Switch to Postgres via `docker-compose.yml` and env overrides.
