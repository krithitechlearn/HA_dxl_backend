# GENERATION NOTES

## Coverage

| US ID | Story Title | API IDs | Generated Classes | Tests |
|-------|-------------|---------|-------------------|-------|
| US-001 | User Authentication | API-001, API-002, API-003 | AuthController, AuthServiceImpl, JwtTokenProvider, JwtAuthenticationFilter, CustomUserDetailsService | AuthControllerTest, AuthServiceImplTest, HaDxlApplicationIntegrationTest |
| US-002 | User Management | API-004 to API-009 | UserController, UserServiceImpl, UserRepository, UserMapper | UserControllerTest, UserServiceImplTest, UserRepositoryTest |
| US-003 | Room Management | API-010 to API-014, API-020 | RoomController, RoomServiceImpl, RoomRepository, RoomMapper | RoomController (via DeviceControllerTest) |
| US-004 | Device Registration & Management | API-015 to API-019 | DeviceController, DeviceServiceImpl, DeviceRepository, DeviceMapper | DeviceControllerTest |
| US-005 | Guest Device Access | API-021, API-022 | DeviceController (access endpoints), UserDeviceAccessRepository | DeviceControllerTest |
| US-006 | Device Command Issuance | API-023, API-024 | DeviceCommandController, DeviceCommandServiceImpl, DeviceCommandRepository, DeviceCommandMapper | DeviceCommandServiceImplTest |
| US-007 | Telemetry Ingestion | API-025, API-026 | TelemetryController, TelemetryServiceImpl, TelemetryRepository, TelemetryMapper | TelemetryServiceImplTest |
| US-008 | Telemetry Visualization | API-026 | TelemetryController, TelemetryServiceImpl | TelemetryServiceImplTest |
| US-009 | Create Automation Rule | API-027, API-028, API-029 | AutomationRuleController, AutomationRuleServiceImpl, AutomationRuleRepository, AutomationRuleMapper | AutomationRuleServiceImplTest |
| US-010 | Manage Automation Rules | API-030 to API-034 | AutomationRuleController, AutomationExecutionServiceImpl, TriggerEvaluator (Strategy) | AutomationRuleServiceImplTest |
| US-011 | Audit Log Viewing | API-035, API-036 | AuditLogController, AuditLogServiceImpl, AuditLogRepository, AuditLogMapper | AuditLogControllerTest |
| US-012 | Device Status Monitoring | API-016, API-017, API-026 | DeviceController, TelemetryController | DeviceControllerTest |

## Deviations and Gaps

| # | LLD Section | What Was Done | Why |
|---|-------------|---------------|-----|
| 1 | LLD 6.3 TelemetryServiceImpl | `purgeOldTelemetry(int)` is both a public method and a `@Scheduled` job. The scheduled job calls `purgeOldTelemetry(retentionDays)` internally. The interface method accepts a parameter for testability. | LLD specifies both a scheduled job and a service method; combined for DRY. |
| 2 | LLD 9 Rate Limiting | Rate limiting on `/api/v1/auth/login` (10 req/min per IP) is NOT implemented. LLD states it requires Bucket4j if confirmed as hard requirement (Q-03 is open). | Q-03 is open; conservative minimum safe behavior applied. Flagged for future sprint. |
| 3 | LLD 5.4 V12 | V12 migration is a no-op placeholder (`SELECT 1`). Telemetry partitioning is deferred per LLD. | LLD explicitly marks this as "optional, deferred". |
| 4 | LLD 6.4 API-016 | GUEST device filtering (only showing granted devices) is not enforced at the query level in this MVP. All authenticated users see all active devices. | Q-02 is open; conservative behavior applied. GUEST access enforcement should be added in a follow-up sprint with proper specification. |
| 5 | LLD 6.1 | `com.ha.dxl.common.audit.AuditLogService` interface is in `common.audit` package but the full implementation (`AuditLogServiceImpl`) is in `audit.service.impl` as specified. The `AuditLogController` uses `AuditLogServiceImpl` directly because the query methods (`getAuditLogs`, `getAuditLogById`) are not on the `AuditLogService` interface (which only defines `logAction`). | LLD section 6.1 places `AuditLogService` in both `common.audit` and `audit.service`; resolved by keeping the write-only interface in `audit.service` and the full impl in `audit.service.impl`. |
| 6 | LLD 9 CSRF | CSRF is disabled as per LLD section 9 explicit decision. | LLD states: "CSRF protection disabled for the REST API (stateless JWT in HttpOnly cookies with SameSite=Strict provides equivalent protection)". |
| 7 | LLD 6.3 DeviceServiceImpl | `getAllDevices` uses simple if-else filter chain rather than JPA Specification. | Q-02 is open regarding max devices; conservative simple approach used. JpaSpecificationExecutor is declared on DeviceRepository for future use. |
| 8 | LLD 11 Frontend Tests | Frontend tests (React Testing Library) are not generated. | LLD section 7 (Frontend) is explicitly out of scope for backend generation. |

## Assumptions Applied

| ID | How Applied |
|----|-------------|
| A-01 | Single household model; no tenant isolation in any entity or query |
| A-02 | JWT in HttpOnly cookies; `ha_access_token` (15 min) and `ha_refresh_token` (7 days); SameSite=Strict set via cookie attributes |
| A-03 | Telemetry ingested via REST POST `/api/v1/telemetry`; no MQTT broker |
| A-04 | Password regex: `^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\-=\[\]{};':,./<>?]).{8,}$` applied in UserCreateRequest and ChangePasswordRequest |
| A-05 | Default page size 20 via `@PageableDefault(size = 20)` on all list endpoints |
| A-06 | DeviceType enum: LIGHT, THERMOSTAT, LOCK, SENSOR, SWITCH, CAMERA |
| A-07 | Automation rules: one trigger → one or more actions; simple trigger-action pairs |
| A-08 | Telemetry retention 90 days; `@Scheduled(cron = "0 0 2 * * *", zone = "UTC")` in TelemetryServiceImpl |
| A-09 | GUEST role assigned by ADMIN; GUEST cannot modify devices or create automations (enforced via `@PreAuthorize`) |
| A-10 | All timestamps stored as `TIMESTAMPTZ` (UTC); `OffsetDateTime` used in Java |
| A-11 | `@Version` on Device and AutomationRule entities; `ObjectOptimisticLockingFailureException` → 409 in GlobalExceptionHandler |
| A-12 | Spring Boot serves REST API only; no static file serving |
| Q-01 | Not implemented (WebSocket deferred) |
| Q-02 | No max device/automation limit enforced; flagged as deviation |
| Q-03 | Device-level API keys not implemented; SYSTEM role used for telemetry ingestion |
| Q-04 | Synchronous command processing; async deferred |
| Q-05 | Audit log export not implemented |

## Configuration Keys

### Environment Variables

| Variable | Description |
|----------|-------------|
| `DB_URL` | JDBC URL for PostgreSQL (e.g., `jdbc:postgresql://localhost:5432/ha_dxl`) |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `DB_POOL_MAX_SIZE` | HikariCP maximum pool size (default: 20) |
| `JWT_SECRET` | HS256 signing secret (minimum 256-bit / 32 characters) |
| `JWT_ACCESS_TOKEN_EXPIRY_MS` | Access token expiry in milliseconds (default: 900000 = 15 min) |
| `JWT_REFRESH_TOKEN_EXPIRY_MS` | Refresh token expiry in milliseconds (default: 604800000 = 7 days) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allowed CORS origins |
| `SERVER_PORT` | HTTP server port (default: 8080) |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (`dev`, `prod`) |
| `TELEMETRY_RETENTION_DAYS` | Days to retain telemetry data (default: 90) |
| `LOG_LEVEL_ROOT` | Root log level (default: INFO) |
| `LOG_LEVEL_APP` | Application log level (default: INFO) |

### application.yml Property Keys

| Key | Description |
|-----|-------------|
| `spring.datasource.url` | Bound from `DB_URL` |
| `spring.datasource.username` | Bound from `DB_USERNAME` |
| `spring.datasource.password` | Bound from `DB_PASSWORD` |
| `spring.datasource.hikari.maximum-pool-size` | Bound from `DB_POOL_MAX_SIZE` |
| `spring.jpa.hibernate.ddl-auto` | Set to `validate` (Flyway owns schema) |
| `spring.flyway.enabled` | `true` |
| `app.jwt.secret` | Bound from `JWT_SECRET` |
| `app.jwt.access-token-expiry-ms` | Bound from `JWT_ACCESS_TOKEN_EXPIRY_MS` |
| `app.jwt.refresh-token-expiry-ms` | Bound from `JWT_REFRESH_TOKEN_EXPIRY_MS` |
| `app.cors.allowed-origins` | Bound from `CORS_ALLOWED_ORIGINS` |
| `app.telemetry.retention-days` | Bound from `TELEMETRY_RETENTION_DAYS` |

## How to Build and Run

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker (for Testcontainers and local Postgres)

### Build
```bash
cd Backend
mvn clean package -DskipTests
```

### Run Tests
```bash
cd Backend
mvn test
# Testcontainers will automatically pull postgres:15-alpine
```

### Local Postgres via Docker
```bash
docker run -d \
  --name ha_dxl_postgres \
  -e POSTGRES_DB=ha_dxl \
  -e POSTGRES_USER=ha_dxl_user \
  -e POSTGRES_PASSWORD=devpassword \
  -p 5432:5432 \
  postgres:15-alpine
```

### Run Application
```bash
export DB_URL=jdbc:postgresql://localhost:5432/ha_dxl
export DB_USERNAME=ha_dxl_user
export DB_PASSWORD=devpassword
export JWT_SECRET=ThisIsAVeryLongSecretKeyForHaDxlBackendApplicationThatMustBeAtLeast256BitsLong
export CORS_ALLOWED_ORIGINS=http://localhost:3000
cd Backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker Compose (Full Stack)
```bash
cd Backend
export DB_PASSWORD=devpassword
export JWT_SECRET=ThisIsAVeryLongSecretKeyForHaDxlBackendApplicationThatMustBeAtLeast256BitsLong
docker-compose up --build
```

## Not Generated

- **Frontend (React TypeScript)**: LLD section 7 is out of scope for backend generation
- **Rate limiting (Bucket4j)**: Q-03 is open; deferred pending confirmation
- **Telemetry partitioning**: V12 migration is a placeholder; deferred per LLD
- **E2E tests (Playwright)**: Deferred to future sprint per LLD section 11
- **GUEST device filtering**: Access enforcement at query level deferred (Q-02 open)
- **Audit log export (CSV/PDF)**: Q-05 is open; not in current scope
- **WebSocket/MQTT**: Deferred per LLD section 2
