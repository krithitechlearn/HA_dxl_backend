-- V11__seed_admin_user.sql
-- Insert default ADMIN user with BCrypt-hashed password
-- Default password: Admin@1234 (BCrypt strength 12)
-- IMPORTANT: Change this password immediately after first deployment
-- Related: US-001, US-002

INSERT INTO users (id, username, email, password_hash, role, is_active, created_by, updated_by)
VALUES (
    gen_random_uuid(),
    'admin',
    'admin@ha-dxl.local',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4tbQIhGCpC',
    'ADMIN',
    TRUE,
    'SYSTEM',
    'SYSTEM'
);
