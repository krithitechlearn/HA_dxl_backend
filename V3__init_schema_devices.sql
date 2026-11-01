-- V3__init_schema_devices.sql
-- Creates the devices table with FKs to rooms and users
-- Related: US-003, US-004

CREATE TABLE devices (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    device_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
    manufacturer VARCHAR(100),
    model_number VARCHAR(100),
    firmware_version VARCHAR(50),
    attributes JSONB DEFAULT '{}',
    room_id UUID,
    assigned_user_id UUID,
    version INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    last_seen_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    CONSTRAINT pk_devices PRIMARY KEY (id),
    CONSTRAINT chk_devices_type CHECK (device_type IN ('LIGHT', 'THERMOSTAT', 'LOCK', 'SENSOR', 'SWITCH', 'CAMERA')),
    CONSTRAINT chk_devices_status CHECK (status IN ('ONLINE', 'OFFLINE', 'ERROR', 'MAINTENANCE')),
    CONSTRAINT fk_devices_room FOREIGN KEY (room_id)
        REFERENCES rooms (id) ON DELETE SET NULL,
    CONSTRAINT fk_devices_assigned_user FOREIGN KEY (assigned_user_id)
        REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX idx_devices_room_id ON devices (room_id);
CREATE INDEX idx_devices_status ON devices (status);
CREATE INDEX idx_devices_type ON devices (device_type);
CREATE INDEX idx_devices_assigned_user ON devices (assigned_user_id);
CREATE INDEX idx_devices_is_active ON devices (is_active);
