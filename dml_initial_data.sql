-- Initial data for 'users' table (ADMIN user)
INSERT INTO users (id, username, email, password_hash, role, is_active, created_at, updated_at, created_by, updated_by)
VALUES (gen_random_uuid(), 'admin', 'admin@example.com', '$2a$12$EXAMPLEHASHFORADMINPASSWORD.aB1c2D3e4F5g6H7i8J9k0L1m2N3o4P5q6R7s8T9u', 'ADMIN', TRUE, NOW(), NOW(), 'system', 'system');
