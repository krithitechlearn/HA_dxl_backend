package com.ha.dxl.device.domain;

import com.ha.dxl.room.domain.Room;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing a registered smart home device.
 * Uses optimistic locking via @Version.
 * @see US-003, US-004, A-11
 */
@Entity
@Table(name = "devices")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", nullable = false, length = 30)
    private DeviceType deviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeviceStatus status = DeviceStatus.OFFLINE;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Column(name = "model_number", length = 100)
    private String modelNumber;

    @Column(name = "firmware_version", length = 50)
    private String firmwareVersion;

    @Type(JsonBinaryType.class)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private Map<String, Object> attributes = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "last_seen_at")
    private OffsetDateTime lastSeenAt;

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
        if (!(o instanceof Device device)) return false;
        return Objects.equals(id, device.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Device{id=" + id + ", name='" + name + "', type=" + deviceType + ", status=" + status + "}";
    }
}
