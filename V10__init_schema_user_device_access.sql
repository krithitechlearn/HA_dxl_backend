-- V10__init_schema_user_device_access.sql
-- Creates the user_device_access table
-- Related: US-005

CREATE TABLE user_device_access (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    device_id UUID NOT NULL,
    access_level VARCHAR(20) NOT NULL DEFAULT 'READ',
    granted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    granted_by_user_id UUID NOT NULL,
    CONSTRAINT pk_user_device_access PRIMARY KEY (id),
    CONSTRAINT uq_user_device_access UNIQUE (user_id, device_id),
    CONSTRAINT chk_user_device_access_level CHECK (access_level IN ('READ', 'CONTROL')),
    CONSTRAINT fk_user_device_access_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_device_access_device FOREIGN KEY (device_id)
        REFERENCES devices (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_device_access_granted_by FOREIGN KEY (granted_by_user_id)
        REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_user_device_access_user ON user_device_access (user_id);
CREATE INDEX idx_user_device_access_device ON user_device_access (device_id);
