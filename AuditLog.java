package com.ha.dxl.audit.domain;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing an immutable audit log entry.
 * No FK constraints - must survive entity deletion.
 * @see US-011
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "actor_username", nullable = false, length = 100)
    private String actorUsername;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "entity_type", nullable = false, length = 100)
    private String entityType;

    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    @Type(JsonBinaryType.class)
    @Column(name = "before_state", columnDefinition = "jsonb")
    private Object beforeState;

    @Type(JsonBinaryType.class)
    @Column(name = "after_state", columnDefinition = "jsonb")
    private Object afterState;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditLog that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AuditLog{id=" + id + ", action='" + action + "', entityType='" + entityType + "', occurredAt=" + occurredAt + "}";
    }
}
