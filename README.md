# Attendance Management System - Backend

This is the **backend** of the Attendance Management System (AMS), built with **Spring Boot**.  
It provides secure REST APIs for attendance tracking, correction requests, and admin approvals.  
The frontend (React) communicates with this service.

---

## ✨ Features

### Authentication
- JWT-based authentication
- Login with email & password

### Employee APIs
- Clock In / Clock Out with location + selfie upload
- View own attendance records by date
- Submit correction requests for attendance errors
- View own correction history

### Admin APIs
- View daywise attendance for any employee
- View attendance summary between date ranges
- Manage correction requests (Approve / Reject)
- Trigger auto-invalidate job for open entries

---

## 🛠 Tech Stack
- Java 17+
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA + H2 (or any SQL database)
- Maven

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/ams-backend.git
cd ams-backend
```

### 2. Configure Application Properties
Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:amsdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: "replace-this-with-a-256-bit-secret-string-or-env-var"
  exp-minutes: 60

app:
  upload-dir: ./uploads
```

### 3. Run the Application
```bash
mvn spring-boot:run
```
The server starts at 👉 [http://localhost:8080](http://localhost:8080)

---

## 📂 Project Structure

```
ams-backend/
│── src/main/java/com/example/ams/
│   ├── controller/         # REST Controllers
│   ├── model/              # Entity models
│   ├── repo/               # Repositories (JPA)
│   ├── security/           # JWT + Spring Security
│   ├── config/             # Config classes
│   └── AmsApplication.java # Main Spring Boot entry
│── src/main/resources/
│   ├── application.yml     # Config file
│   └── schema.sql          # (Optional) DB init
│── pom.xml
│── README.md
```

---

## 🔗 API Endpoints

### Auth
- `POST /auth/login` → Login and receive JWT

### Attendance
- `POST /attendance/clock-in` → Employee clock-in (with selfie & location)
- `POST /attendance/clock-out` → Employee clock-out
- `GET /attendance/me?date=YYYY-MM-DD` → Employee's own attendance

### Corrections
- `POST /attendance/corrections` → Create correction request
- `GET /attendance/corrections?mine=true` → List own corrections
- `GET /attendance/corrections/all?status=PENDING` → Admin view pending corrections
- `PATCH /attendance/corrections/{id}/decision` → Approve/Reject

### Admin
- `GET /admin/attendance?employeeId=...&date=YYYY-MM-DD` → Daywise attendance
- `GET /admin/attendance/summary?from=YYYY-MM-DD&to=YYYY-MM-DD` → Range summary
- `POST /admin/attendance/auto-invalidate` → Trigger invalidation job

---

## 🔑 Sample Users

- **Employee:**  
  - email: `employee@ams.local`  
  - password: `demo`

- **Admin/Approver:**  
  - email: `approver@ams.local`  
  - password: `demo`

---

## 🧪 Testing with Postman

1. Login via `/auth/login` → Get `accessToken`  
2. Use token in `Authorization: Bearer <token>` header  
3. Call employee/admin APIs accordingly

---

## 📦 Build & Deployment

Build JAR:
```bash
mvn clean package
```
Run JAR:
```bash
java -jar target/ams-0.0.1-SNAPSHOT.jar
```

Deploy anywhere (AWS EC2, Azure, Heroku, Docker, etc).

---

## 📜 License
This project is licensed under the **MIT License**.
