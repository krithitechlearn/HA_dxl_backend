package com.ha.dxl.telemetry.service;

import com.ha.dxl.telemetry.dto.TelemetryIngestRequest;
import com.ha.dxl.telemetry.dto.TelemetryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Service interface for telemetry operations.
 * @see US-007, US-008
 */
public interface TelemetryService {

    /** @see API-025 */
    TelemetryResponse ingestTelemetry(TelemetryIngestRequest request);

    /** @see API-026 */
    Page<TelemetryResponse> getDeviceTelemetry(UUID deviceId, OffsetDateTime from, OffsetDateTime to,
                                                String metricName, Pageable pageable);

    /**
     * Purges telemetry records older than retentionDays days.
     * @see A-08
     */
    int purgeOldTelemetry(int retentionDays);
}
