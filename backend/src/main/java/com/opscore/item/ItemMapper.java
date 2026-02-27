package com.opscore.item;

import com.opscore.item.dto.ItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "tenant.id", target = "tenantId")
    @Mapping(source = "assignedToUser.id", target = "assignedToUserId")
    @Mapping(source = "assignedToUser.email", target = "assignedToUserName")
    @Mapping(source = "createdByUser.id", target = "createdByUserId")
    @Mapping(source = "createdByUser.email", target = "createdByUserName")
    ItemDto toDto(Item item);
}
