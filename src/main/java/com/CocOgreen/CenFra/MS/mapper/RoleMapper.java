package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.RoleDTO;
import com.CocOgreen.CenFra.MS.entity.Role;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO toDTO(Role role);

    @InheritInverseConfiguration
    @Mapping(target = "roleId", ignore = true)
    Role toEntity(RoleDTO dto);
}
