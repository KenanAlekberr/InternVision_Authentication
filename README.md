# auth-system-0.0.1-SNAPSHOT.jar

___

# 🔐 User Authentication System

A secure and scalable user authentication system built with Spring Boot and Gradle, featuring full user management, JWT
authentication, email OTP verification, and Dockerized deployment.

This project demonstrates modern authentication best practices using Spring Security, JWT, Redis/Redisson, Liquibase,
and PostgreSQL.
All secrets and credentials are safely managed using a .env file.
___

# 🚀 Features
___ 
# 🔑 Authentication & Authorization

- Register – User signup with validation
- Login – JWT-based authentication
- Logout – Token invalidation
- Change Password – Authenticated password update
- Forgot Password – Sends OTP code to user’s Gmail for password reset
- Verify OTP – Email verification via one-time password
- Reset Password – Password recovery using OTP verification

# 👤 User Management (CRUD)

- Create, Read, Update, Delete operations for user entities
- Role-based access control
- DTO structure for request/response mapping

___

# 🧩 Technologies Used

```
| Technology                   | Purpose                         |
| ---------------------------- | ------------------------------- |
| **Spring Boot**              | Core framework                  |
| **Spring Security**          | Authentication & authorization  |
| **JWT (JSON Web Token)**     | Stateless authentication        |
| **Redis / Redisson**         | Caching & OTP storage           |
| **Liquibase**                | Database migration management   |
| **PostgreSQL**               | Primary relational database     |
| **Docker**                   | Containerization & deployment   |
| **Swagger / OpenAPI**        | API documentation               |
| **Jakarta Mail**             | Sending OTP codes to Gmail      |
| **Jakarta Validation**       | Input validation                |
| **Global Exception Handler** | Centralized error handling      |
| **Lombok**                   | Boilerplate code reduction      |
| **Gradle**                   | Build and dependency management |
```

___

# ⚙️ Configuration

All sensitive information such as secret keys, passwords, and database credentials are stored securely in a .env file.
___

# 🧠 Security Highlights

- Passwords are encrypted using BCrypt
- Access and Refresh tokens managed separately
- Environment variables store all secrets securely
- Exception handling via centralized @ControllerAdvice
___

# 🔄 OTP & Redis Integration

- OTPs are stored temporarily in Redis with automatic expiration
- Used for:
     - Account verification
     - Password reset flow
- Redisson is used for distributed locks and async operations