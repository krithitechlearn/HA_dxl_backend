
-- Migration Notes:
-- 1. Ensure the 'ha_dxl' database exists before running these scripts.
-- 2. The 'gen_random_uuid()' function requires the 'uuid-ossp' extension to be enabled: CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- 3. The provided DML for the 'admin' user uses a placeholder password hash. Replace it with a securely generated BCrypt hash for your production environment.
-- 4. DCL grants are based on the role-permission matrix. Fine-tune as needed for least privilege.
-- 5. Optimistic locking and email format validation are handled at the application layer, not strictly enforced by database constraints.
-- 6. Telemetry partitioning (V12 in LLD) is optional and not included in this initial DDL. Consider implementing it for large-scale telemetry data.
