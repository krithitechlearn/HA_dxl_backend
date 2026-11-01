-- V6__init_schema_automation_rules.sql
-- Creates the automation_rules table
-- Related: US-009, US-010

CREATE TABLE automation_rules (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    owner_user_id UUID NOT NULL,
    trigger_type VARCHAR(50) NOT NULL,
    trigger_config JSONB NOT NULL DEFAULT '{}',
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 0,
    last_triggered_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    CONSTRAINT pk_automation_rules PRIMARY KEY (id),
    CONSTRAINT chk_automation_rules_trigger_type CHECK (trigger_type IN ('SCHEDULE', 'DEVICE_STATE', 'SUNRISE', 'SUNSET', 'MANUAL')),
    CONSTRAINT fk_automation_rules_owner FOREIGN KEY (owner_user_id)
        REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_automation_rules_owner ON automation_rules (owner_user_id);
CREATE INDEX idx_automation_rules_enabled ON automation_rules (is_enabled);
CREATE INDEX idx_automation_rules_trigger_type ON automation_rules (trigger_type);
