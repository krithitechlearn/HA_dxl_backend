package com.ha.dxl.telemetry.mapper;

import com.ha.dxl.telemetry.domain.Telemetry;
import com.ha.dxl.telemetry.dto.TelemetryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Telemetry entity and DTOs.
 * @see US-007, US-008
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TelemetryMapper {

    @Mapping(target = "deviceId", source = "device.id")
    TelemetryResponse toResponse(Telemetry telemetry);
}
