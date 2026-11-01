-- V8__init_schema_automation_execution_logs.sql
-- Creates the automation_execution_logs table
-- Related: US-010

CREATE TABLE automation_execution_logs (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    automation_rule_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    trigger_detail VARCHAR(500),
    error_message VARCHAR(1000),
    executed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_automation_execution_logs PRIMARY KEY (id),
    CONSTRAINT chk_automation_exec_logs_status CHECK (status IN ('SUCCESS', 'PARTIAL_FAILURE', 'FAILURE')),
    CONSTRAINT fk_automation_exec_logs_rule FOREIGN KEY (automation_rule_id)
        REFERENCES automation_rules (id) ON DELETE CASCADE
);

CREATE INDEX idx_automation_exec_logs_rule_id ON automation_execution_logs (automation_rule_id);
CREATE INDEX idx_automation_exec_logs_executed_at ON automation_execution_logs (executed_at DESC);
