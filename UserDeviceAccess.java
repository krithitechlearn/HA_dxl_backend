package com.ha.dxl.device.domain;

import com.ha.dxl.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA entity representing a user's access grant to a specific device.
 * @see US-005
 */
@Entity
@Table(name = "user_device_access")
@Getter
@Setter
public class UserDeviceAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false, length = 20)
    private AccessLevel accessLevel = AccessLevel.READ;

    @Column(name = "granted_at", nullable = false)
    private OffsetDateTime grantedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by_user_id", nullable = false)
    private User grantedByUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserDeviceAccess that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserDeviceAccess{id=" + id + ", accessLevel=" + accessLevel + "}";
    }
}
