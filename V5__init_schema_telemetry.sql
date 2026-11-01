-- V5__init_schema_telemetry.sql
-- Creates the telemetry table
-- Related: US-007, US-008

CREATE TABLE telemetry (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    device_id UUID NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value NUMERIC(15, 4) NOT NULL,
    unit VARCHAR(30),
    raw_payload JSONB,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_telemetry PRIMARY KEY (id),
    CONSTRAINT fk_telemetry_device FOREIGN KEY (device_id)
        REFERENCES devices (id) ON DELETE CASCADE
);

CREATE INDEX idx_telemetry_device_recorded ON telemetry (device_id, recorded_at DESC);
CREATE INDEX idx_telemetry_recorded_at ON telemetry (recorded_at DESC);
