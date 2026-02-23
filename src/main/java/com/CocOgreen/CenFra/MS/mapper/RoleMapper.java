package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.RoleDTO;
import com.CocOgreen.CenFra.MS.entity.Role;
import org.mapstruct.Mapper;
<<<<<<< feature/Login-Authorize
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(source = "role.roleName", target = "roleName")

    RoleDTO toDTO(Role role);
}
