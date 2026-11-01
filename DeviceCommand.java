package com.ha.dxl.device.domain;

import com.ha.dxl.user.domain.User;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing a command issued to a device.
 * @see US-006
 */
@Entity
@Table(name = "device_commands")
@Getter
@Setter
public class DeviceCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_user_id", nullable = false)
    private User issuedByUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "command_type", nullable = false, length = 50)
    private CommandType commandType;

    @Type(JsonBinaryType.class)
    @Column(name = "command_payload", columnDefinition = "jsonb")
    private Map<String, Object> commandPayload = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CommandStatus status = CommandStatus.PENDING;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "issued_at", nullable = false)
    private OffsetDateTime issuedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceCommand that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DeviceCommand{id=" + id + ", commandType=" + commandType + ", status=" + status + "}";
    }
}
