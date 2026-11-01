package com.ha.dxl.device.mapper;

import com.ha.dxl.device.domain.DeviceCommand;
import com.ha.dxl.device.dto.DeviceCommandResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for DeviceCommand entity and DTOs.
 * @see US-006
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeviceCommandMapper {

    @Mapping(target = "deviceId", source = "device.id")
    @Mapping(target = "issuedByUserId", source = "issuedByUser.id")
    DeviceCommandResponse toResponse(DeviceCommand command);
}
