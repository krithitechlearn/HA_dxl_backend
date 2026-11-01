package com.ha.dxl.automation.domain;

import com.ha.dxl.device.domain.Device;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing a single action within an automation rule.
 * @see US-009, US-010
 */
@Entity
@Table(name = "automation_actions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AutomationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "automation_rule_id", nullable = false)
    private AutomationRule automationRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_device_id", nullable = false)
    private Device targetDevice;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 50)
    private ActionType actionType;

    @Type(JsonBinaryType.class)
    @Column(name = "action_payload", columnDefinition = "jsonb")
    private Map<String, Object> actionPayload = new HashMap<>();

    @Column(name = "execution_order", nullable = false)
    private int executionOrder = 1;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutomationAction that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AutomationAction{id=" + id + ", actionType=" + actionType + ", executionOrder=" + executionOrder + "}";
    }
}
