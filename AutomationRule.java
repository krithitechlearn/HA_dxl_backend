package com.ha.dxl.automation.domain;

import com.ha.dxl.user.domain.User;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing an automation rule.
 * Uses optimistic locking via @Version.
 * @see US-009, US-010, A-11
 */
@Entity
@Table(name = "automation_rules")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AutomationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User ownerUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_type", nullable = false, length = 50)
    private TriggerType triggerType;

    @Type(JsonBinaryType.class)
    @Column(name = "trigger_config", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> triggerConfig = new HashMap<>();

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled = true;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "last_triggered_at")
    private OffsetDateTime lastTriggeredAt;

    @OneToMany(mappedBy = "automationRule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("executionOrder ASC")
    private List<AutomationAction> actions = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false, length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutomationRule that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AutomationRule{id=" + id + ", name='" + name + "', triggerType=" + triggerType + ", isEnabled=" + isEnabled + "}";
    }
}
