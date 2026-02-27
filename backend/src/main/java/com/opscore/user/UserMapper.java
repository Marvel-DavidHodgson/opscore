package com.opscore.user;

import com.opscore.user.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "tenant.id", target = "tenantId")
    UserDto toDto(User user);
}
