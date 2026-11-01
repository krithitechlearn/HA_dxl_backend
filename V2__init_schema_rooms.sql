-- V2__init_schema_rooms.sql
-- Creates the rooms table with FK to users
-- Related: US-003

CREATE TABLE rooms (
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    floor_level VARCHAR(50),
    created_by_user_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100) NOT NULL,
    updated_by VARCHAR(100) NOT NULL,
    CONSTRAINT pk_rooms PRIMARY KEY (id),
    CONSTRAINT uq_rooms_name UNIQUE (name),
    CONSTRAINT fk_rooms_created_by_user FOREIGN KEY (created_by_user_id)
        REFERENCES users (id) ON DELETE RESTRICT
);

CREATE INDEX idx_rooms_name ON rooms (name);
