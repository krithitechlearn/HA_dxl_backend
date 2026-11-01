package com.ha.dxl.device.mapper;

import com.ha.dxl.device.domain.Device;
import com.ha.dxl.device.dto.DeviceCreateRequest;
import com.ha.dxl.device.dto.DeviceResponse;
import com.ha.dxl.device.dto.DeviceUpdateRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for Device entity and DTOs.
 * @see US-003, US-004
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DeviceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "OFFLINE")
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "version", constant = "0")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "lastSeenAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deviceType", expression = "java(com.ha.dxl.device.domain.DeviceType.valueOf(request.deviceType()))")
    Device toEntity(DeviceCreateRequest request);

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "assignedUserId", source = "assignedUser.id")
    DeviceResponse toResponse(Device device);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deviceType", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "room", ignore = true)
    @Mapping(target = "assignedUser", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "lastSeenAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(DeviceUpdateRequest request, @MappingTarget Device device);
}
