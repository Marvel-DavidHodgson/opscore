package com.opscore.tenant;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    
    TenantDto toDto(Tenant tenant);
}
