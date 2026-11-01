-- V4__init_schema_device_commands.sql
-- Creates the device_commands table
-- Related: US-006

CREATE TABLE device_commands (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    device_id UUID NOT NULL,
    issued_by_user_id UUID NOT NULL,
    command_type VARCHAR(50) NOT NULL,
    command_payload JSONB DEFAULT '{}',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message VARCHAR(500),
    issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMPTZ,
    CONSTRAINT pk_device_commands PRIMARY KEY (id),
    CONSTRAINT chk_device_commands_type CHECK (command_type IN ('TURN_ON', 'TURN_OFF', 'SET_TEMPERATURE', 'LOCK', 'UNLOCK', 'SET_BRIGHTNESS', 'SET_COLOR')),
    CONSTRAINT chk_device_commands_status CHECK (status IN ('PENDING', 'SENT', 'ACKNOWLEDGED', 'FAILED')),
    CONSTRAINT fk_device_commands_device FOREIGN KEY (device_id)
        REFERENCES devices (id) ON DELETE CASCADE,
    CONSTRAINT fk_device_commands_user FOREIGN KEY (issued_by_user_id)
        REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_device_commands_device_id ON device_commands (device_id);
CREATE INDEX idx_device_commands_issued_at ON device_commands (issued_at DESC);
CREATE INDEX idx_device_commands_status ON device_commands (status);
