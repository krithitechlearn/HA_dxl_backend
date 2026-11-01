package com.ha.dxl.telemetry.service.impl;

import com.ha.dxl.device.domain.Device;
import com.ha.dxl.device.repository.DeviceRepository;
import com.ha.dxl.exception.ResourceNotFoundException;
import com.ha.dxl.telemetry.domain.Telemetry;
import com.ha.dxl.telemetry.dto.TelemetryIngestRequest;
import com.ha.dxl.telemetry.dto.TelemetryResponse;
import com.ha.dxl.telemetry.mapper.TelemetryMapper;
import com.ha.dxl.telemetry.repository.TelemetryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TelemetryServiceImpl.
 * @see US-007
 */
@ExtendWith(MockitoExtension.class)
class TelemetryServiceImplTest {

    @Mock
    private TelemetryRepository telemetryRepository;
    @Mock
    private DeviceRepository deviceRepository;
    @Mock
    private TelemetryMapper telemetryMapper;

    @InjectMocks
    private TelemetryServiceImpl telemetryService;

    private UUID deviceId;
    private Device testDevice;

    @BeforeEach
    void setUp() {
        deviceId = UUID.randomUUID();
        testDevice = new Device();
        testDevice.setId(deviceId);
        testDevice.setName("Test Sensor");
    }

    @Test
    @DisplayName("US-007: ingestTelemetry_whenDeviceNotFound_throwsResourceNotFoundException")
    void ingestTelemetry_whenDeviceNotFound_throwsResourceNotFoundException() {
        TelemetryIngestRequest request = new TelemetryIngestRequest(
                deviceId, "temperature", BigDecimal.valueOf(22.5), "Celsius",
                null, OffsetDateTime.now());
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> telemetryService.ingestTelemetry(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("US-007: ingestTelemetry_updatesDeviceLastSeenAt")
    void ingestTelemetry_updatesDeviceLastSeenAt() {
        TelemetryIngestRequest request = new TelemetryIngestRequest(
                deviceId, "temperature", BigDecimal.valueOf(22.5), "Celsius",
                null, OffsetDateTime.now());
        Telemetry savedTelemetry = new Telemetry();
        savedTelemetry.setId(UUID.randomUUID());
        savedTelemetry.setDevice(testDevice);
        savedTelemetry.setMetricName("temperature");
        savedTelemetry.setMetricValue(BigDecimal.valueOf(22.5));
        savedTelemetry.setRecordedAt(OffsetDateTime.now());

        TelemetryResponse expectedResponse = new TelemetryResponse(
                savedTelemetry.getId(), deviceId, "temperature",
                BigDecimal.valueOf(22.5), "Celsius", null, OffsetDateTime.now());

        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(testDevice));
        when(telemetryRepository.save(any())).thenReturn(savedTelemetry);
        doNothing().when(deviceRepository).updateLastSeenAt(any(), any());
        when(telemetryMapper.toResponse(savedTelemetry)).thenReturn(expectedResponse);

        TelemetryResponse result = telemetryService.ingestTelemetry(request);

        assertThat(result).isNotNull();
        assertThat(result.metricName()).isEqualTo("temperature");
        verify(deviceRepository).updateLastSeenAt(eq(deviceId), any());
    }
}
