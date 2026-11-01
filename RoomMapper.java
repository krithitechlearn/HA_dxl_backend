package com.ha.dxl.room.mapper;

import com.ha.dxl.room.domain.Room;
import com.ha.dxl.room.dto.RoomCreateRequest;
import com.ha.dxl.room.dto.RoomResponse;
import com.ha.dxl.room.dto.RoomUpdateRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for Room entity and DTOs.
 * @see US-003
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Room toEntity(RoomCreateRequest request);

    @Mapping(target = "createdByUserId", source = "createdByUser.id")
    RoomResponse toResponse(Room room);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdByUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(RoomUpdateRequest request, @MappingTarget Room room);
}
