# HA DXL Backend

Home Automation Device Experience Layer (DXL) Backend - Java 21 / Spring Boot 3.x REST API.

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker & Docker Compose

### Run Locally with Docker Compose

```bash
cd Backend
export DB_PASSWORD=devpassword
export JWT_SECRET=ThisIsAVeryLongSecretKeyForHaDxlBackendApplicationThatMustBeAtLeast256BitsLong
docker-compose up -d
```

API available at: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`
Health: `http://localhost:8080/actuator/health`

### Run with Maven

```bash
cd Backend
export DB_URL=jdbc:postgresql://localhost:5432/ha_dxl
export DB_USERNAME=ha_dxl_user
export DB_PASSWORD=devpassword
export JWT_SECRET=ThisIsAVeryLongSecretKeyForHaDxlBackendApplicationThatMustBeAtLeast256BitsLong
export CORS_ALLOWED_ORIGINS=http://localhost:3000
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run Tests

```bash
cd Backend
mvn test
```

### Default Admin Credentials
- Username: `admin`
- Password: `Admin@1234`
- **Change immediately after first deployment!**

## API Documentation

OpenAPI docs: `http://localhost:8080/v3/api-docs`
Swagger UI: `http://localhost:8080/swagger-ui.html`

## Architecture

4-layer modular monolith: Controller → Service → Repository → Entity

Modules: auth, user, room, device, telemetry, automation, audit
