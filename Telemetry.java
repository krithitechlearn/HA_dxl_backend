package com.ha.dxl.telemetry.domain;

import com.ha.dxl.device.domain.Device;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing a telemetry reading from a device.
 * @see US-007, US-008
 */
@Entity
@Table(name = "telemetry")
@Getter
@Setter
public class Telemetry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_value", nullable = false, precision = 15, scale = 4)
    private BigDecimal metricValue;

    @Column(name = "unit", length = 30)
    private String unit;

    @Type(JsonBinaryType.class)
    @Column(name = "raw_payload", columnDefinition = "jsonb")
    private Map<String, Object> rawPayload = new HashMap<>();

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Telemetry that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Telemetry{id=" + id + ", metricName='" + metricName + "', recordedAt=" + recordedAt + "}";
    }
}
