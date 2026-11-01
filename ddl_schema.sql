CREATE TABLE users (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by VARCHAR(100) NOT NULL,
  updated_by VARCHAR(100) NOTnULL
);
ALTER TABLE users
  ADD CONSTRAINT chk_users_length
  CHECK (LENGTH(username) >= 3);
ALTER TABLE users
  ADD CONSTRAINT chk_users_role
  CHECK (role IN ('ADMIN', 'USER', 'GUEST'));
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_role ON users (role);
CREATE TABLE rooms (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  description VARCHAR(500) NULL,
  floor_level VARCHAR(50) NULL,
  created_by_user_id UUID NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by VARCHAR(100) NOT NULL,
  updated_by VARCHAR(100) NOT NULL
);
ALTER TABLE rooms
  ADD CONSTRAINT fk_rooms_created_by_user_id
  FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE RESTRICT;
CREATE INDEX idx_rooms_name ON rooms (name);
CREATE TABLE devices (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  device_type VARCHAR(30) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
  manufacturer VARCHAR(100) NULL,
  model_number VARCHAR(100) NULL,
  firmware_version VARCHAR(50) NULL,
  attributes JSONB NULL DEFAULT '{}',
  room_id UUID NULL,
  assigned_user_id UUID NULL,
  version INTEGER NOT NULL DEFAULT 0,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  last_seen_at TIMESTAMPTZ NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by VARCHAR(100) NOT NULL,
  updated_by VARCHAR(100) NOT NULL
);
ALTER TABLE devices
  ADD CONSTRAINT fk_devices_room_id
  FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE SET NULL;
ALTER TABLE devices
  ADD CONSTRAINT fk_devices_assigned_user_id
  FOREIGN KEY (assigned_user_id) REFERENCES users(id) ON DELETE SET NULL;
ALTER TABLE devices
  ADD CONSTRAINT chk_devices_device_type
  CHECK (device_type IN ('LIGHT', 'THERMOSTAT', 'LOCK', 'SENSOR', 'SWITCH', 'CAMERA'));
ALTER TABLE devices
  ADD CONSTRAINT chk_devices_status
  CHECK (status IN ('ONLINE', 'OFFLINE', 'ERROR', 'MAINTENANCE'));
CREATE INDEX idx_devices_room_id ON devices (room_id);
CREATE INDEX idx_devices_status ON devices (status);
CREATE INDEX idx_devices_type ON devices (device_type);
CREATE INDEX idx_devices_assigned_user ON devices (assigned_user_id);
CREATE INDEX idx_devices_is_active ON devices (is_active);
CREATE TABLE device_commands (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  device_id UUID NOT NULL,
  issued_by_user_id UUID NOT NULL,
  command_type VARCHAR(50) NOT NULL,
  command_payload JSONB NULL DEFAULT '{}',
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  error_message VARCHAR(500) NULL,
  issued_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  completed_at TIMESTAMPTZ NULL
);
ALTER TABLE device_commands
  ADD CONSTRAINT fk_device_commands_device_id
  FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE;
ALTER TABLE device_commands
  ADD CONSTRAINT fk_device_commands_issued_by_user_id
  FOREIGN KEY (issued_by_user_id) REFERENCES users(id) ON DELETE RESTRICT;
ALTER TABLE device_commands
  ADD CONSTRAINT chk_device_commands_command_type
  CHECK (command_type IN ('TURN_ON', 'TURN_OFF', 'SET_TEMPERATURE', 'LOCK', 'UNLOCK', 'SET_BRIGHTNESS', 'SET_COLOR'));
ALTER TABLE device_commands
  ADD CONSTRAINT chk_device_commands_status
  CHECK (status IN ('PENDING', 'SENT', 'ACKNOWLEDGED', 'FAILED'));
CREATE INDEX idx_device_commands_device_id ON device_commands (device_id);
CREATE INDEX idx_device_commands_issued_at ON device_commands (issued_at DESC);
CREATE INDEX idx_device_commands_status ON device_commands (status);
CREATE TABLE telemetry (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  device_id UUID NOT NULL,
  metric_name VARCHAR(100) NOT NULL,
  metric_value NUMERIC(15,4) NOT NULL,
  unit VARCHAR(30) NULL,
  raw_payload JSONB NULL,
  recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
ALTER TABLE telemetry
  ADD CONSTRAINT fk_telemetry_device_id
  FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE;
CREATE INDEX idx_telemetry_device_recorded ON telemetry (device_id, recorded_at DESC);
CREATE INDEX idx_telemetry_recorded_at ON telemetry (recorded_at DESC);
CREATE TABLE automation_rules (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(500) NULL,
  owner_user_id UUID NOT NULL,
  trigger_type VARCHAR(50) NOT NULL,
  trigger_config JSONB NOT NULL DEFAULT '{}',
  is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
  version INTEGER NOT NULL DEFAULT 0,
  last_triggered_at TIMESTAMPTZ NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  created_by VARCHAR(100) NOT NULL,
  updated_by VARCHAR(100) NOT NULL
);
ALTER TABLE automation_rules
  ADD CONSTRAINT fk_automation_rules_owner_user_id
  FOREIGN KEY (owner_user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE automation_rules
  ADD CONSTRAINT chk_automation_rules_trigger_type
  CHECK (trigger_type IN ('SCHEDULE', 'DEVICE_STATE', 'SUNRISE', 'SUNSET', 'MANUAL'));
CREATE INDEX idx_automation_rules_owner ON automation_rules (owner_user_id);
CREATE INDEX idx_automation_rules_enabled ON automation_rules (is_enabled);
CREATE INDEX idx_automation_rules_trigger_type ON automation_rules (trigger_type);
CREATE TABLE automation_actions (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  automation_rule_id UUID NOT NULL,
  target_device_id UUID NOT NULL,
  action_type VARCHAR(50) NOT NULL,
  action_payload JSONB NULL DEFAULT '{}',
  execution_order INTEGER NOT NULL DEFAULT 1,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
ALTER TABLE automation_actions
  ADD CONSTRAINT fk_automation_actions_automation_rule_id
  FOREIGN KEY (automation_rule_id) REFERENCES automation_rules(id) ON DELETE CASCADE;
ALTER TABLE automation_actions
  ADD CONSTRAINT fk_automation_actions_target_device_id
  FOREIGN KEY (target_device_id) REFERENCES devices(id) ON DELETE RESTRICT;
ALTER TABLE automation_actions
  ADD CONSTRAINT chk_automation_actions_action_type
  CHECK (action_type IN ('TURN_ON', 'TURN_OFF', 'SET_TEMPERATURE', 'LOCK', 'UNLOCK', 'SET_BRIGHTNESS', 'SET_COLOR'));
ALTER TABLE automation_actions
  ADD CONSTRAINT chk_automation_actions_execution_order
  CHECK (execution_order >= 1);
CREATE INDEX idx_automation_actions_rule_id ON automation_actions (automation_rule_id);
CREATE TABLE automation_execution_logs (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  automation_rule_id UUID NOT NULL,
  status VARCHAR(20) NOT NULL,
  trigger_detail VARCHAR(500) NULL,
  error_message VARCHAR(1000) NULL,
  executed_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
ALTER TABLE automation_execution_logs
  ADD CONSTRAINT fk_automation_execution_logs_automation_rule_id
  FOREIGN KEY (automation_rule_id) REFERENCES automation_rules(id) ON DELETE CASCADE;
ALTER TABLE automation_execution_logs
  ADD CONSTRAINT chk_automation_execution_logs_status
  CHECK (status IN ('SUCCESS', 'PARTIAL_FAILURE', 'FAILURE'));
CREATE INDEX idx_automation_exec_logs_rule_id ON automation_execution_logs (automation_rule_id);
CREATE INDEX idx_automation_exec_logs_executed_at ON automation_execution_logs (executed_at DESC);
CREATE TABLE audit_logs (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  actor_username VARCHAR(100) NOT NULL,
  action VARCHAR(100) NOT NULL,
  entity_type VARCHAR(100) NOT NULL,
  entity_id VARCHAR(100) NOT NULL,
  before_state JSONB NULL,
  after_state JSONB NULL,
  ip_address VARCHAR(45) NULL,
  occurred_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_audit_logs_actor ON audit_logs (actor_username);
CREATE INDEX idx_audit_logs_entity ON audit_logs (entity_type, entity_id);
CREATE INDEX idx_audit_logs_occurred_at ON audit_logs (occurred_at DESC);
CREATE TABLE user_device_access (
  id UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
  user_id UUID NOT NULL,
  device_id UUID NOT NULL,
  access_level VARCHAR(20) NOT NULL DEFAULT 'READ',
  granted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  granted_by_user_id UUID NOT NULL,
  UNIQUE (user_id, device_id)
);
ALTER TABLE user_device_access
  ADD CONSTRAINT fk_user_device_access_user_id
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE user_device_access
  ADD CONSTRAINT fk_user_device_access_device_id
  FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE;
ALTER TABLE user_device_access
  ADD CONSTRAINT fk_user_device_access_granted_by_user_id
  FOREIGN KEY (granted_by_user_id) REFERENCES users(id) ON DELETE RESTRICT;
ALTER TABLE user_device_access
  ADD CONSTRAINT chk_user_device_access_access_level
  CHECK (access_level IN ('READ', 'CONTROL'));
CREATE INDEX idx_user_device_access_user ON user_device_access (user_id);
CREATE INDEX idx_user_device_access_device ON user_device_access (device_id);