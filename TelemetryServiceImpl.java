package com.ha.dxl.telemetry.service.impl;

import com.ha.dxl.device.domain.Device;
import com.ha.dxl.device.repository.DeviceRepository;
import com.ha.dxl.exception.ResourceNotFoundException;
import com.ha.dxl.telemetry.domain.Telemetry;
import com.ha.dxl.telemetry.dto.TelemetryIngestRequest;
import com.ha.dxl.telemetry.dto.TelemetryResponse;
import com.ha.dxl.telemetry.mapper.TelemetryMapper;
import com.ha.dxl.telemetry.repository.TelemetryRepository;
import com.ha.dxl.telemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Implementation of TelemetryService.
 * Includes scheduled purge job running nightly at 02:00 UTC.
 * @see US-007, US-008, A-08
 */
@Service
@RequiredArgsConstructor
public class TelemetryServiceImpl implements TelemetryService {

    private static final Logger log = LoggerFactory.getLogger(TelemetryServiceImpl.class);

    private final TelemetryRepository telemetryRepository;
    private final DeviceRepository deviceRepository;
    private final TelemetryMapper telemetryMapper;

    @Value("${app.telemetry.retention-days:90}")
    private int retentionDays;

    @Override
    @Transactional
    public TelemetryResponse ingestTelemetry(TelemetryIngestRequest request) {
        Device device = deviceRepository.findById(request.deviceId())
                .orElseThrow(() -> new ResourceNotFoundException("Device", request.deviceId().toString()));

        Telemetry telemetry = new Telemetry();
        telemetry.setDevice(device);
        telemetry.setMetricName(request.metricName());
        telemetry.setMetricValue(request.metricValue());
        telemetry.setUnit(request.unit());
        telemetry.setRawPayload(request.rawPayload() != null ? request.rawPayload() : new java.util.HashMap<>());
        telemetry.setRecordedAt(request.recordedAt());

        Telemetry saved = telemetryRepository.save(telemetry);

        // Update device last_seen_at and set status to ONLINE
        deviceRepository.updateLastSeenAt(device.getId(), OffsetDateTime.now());
        log.info("Telemetry ingested: deviceId={}, metric={}", device.getId(), request.metricName());
        return telemetryMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TelemetryResponse> getDeviceTelemetry(UUID deviceId, OffsetDateTime from, OffsetDateTime to,
                                                       String metricName, Pageable pageable) {
        if (!deviceRepository.existsById(deviceId)) {
            throw new ResourceNotFoundException("Device", deviceId.toString());
        }
        return telemetryRepository.findByDeviceIdAndFilters(deviceId, from, to, metricName, pageable)
                .map(telemetryMapper::toResponse);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    public int purgeOldTelemetry(int retentionDaysParam) {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(retentionDaysParam);
        int deleted = telemetryRepository.deleteByRecordedAtBefore(cutoff);
        log.info("Telemetry purge completed: deleted={} records older than {} days", deleted, retentionDaysParam);
        return deleted;
    }

    /**
     * Scheduled nightly purge using configured retention days.
     * @see A-08
     */
    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    public void scheduledPurge() {
        purgeOldTelemetry(retentionDays);
    }
}
