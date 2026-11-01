package com.ha.dxl.automation.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing an execution log entry for an automation rule.
 * @see US-010
 */
@Entity
@Table(name = "automation_execution_logs")
@Getter
@Setter
public class AutomationExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "automation_rule_id", nullable = false)
    private AutomationRule automationRule;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AutomationExecutionStatus status;

    @Column(name = "trigger_detail", length = 500)
    private String triggerDetail;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "executed_at", nullable = false)
    private OffsetDateTime executedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AutomationExecutionLog that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AutomationExecutionLog{id=" + id + ", status=" + status + ", executedAt=" + executedAt + "}";
    }
}
