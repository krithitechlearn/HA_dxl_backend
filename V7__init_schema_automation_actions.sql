-- V7__init_schema_automation_actions.sql
-- Creates the automation_actions table
-- Related: US-009, US-010

CREATE TABLE automation_actions (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    automation_rule_id UUID NOT NULL,
    target_device_id UUID NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    action_payload JSONB DEFAULT '{}',
    execution_order INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_automation_actions PRIMARY KEY (id),
    CONSTRAINT chk_automation_actions_type CHECK (action_type IN ('TURN_ON', 'TURN_OFF', 'SET_TEMPERATURE', 'LOCK', 'UNLOCK', 'SET_BRIGHTNESS', 'SET_COLOR')),
    CONSTRAINT chk_automation_actions_execution_order CHECK (execution_order >= 1),
    CONSTRAINT fk_automation_actions_rule FOREIGN KEY (automation_rule_id)
        REFERENCES automation_rules (id) ON DELETE CASCADE,
    CONSTRAINT fk_automation_actions_device FOREIGN KEY (target_device_id)
        REFERENCES devices (id) ON DELETE RESTRICT
);

CREATE INDEX idx_automation_actions_rule_id ON automation_actions (automation_rule_id);
