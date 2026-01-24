package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.UsersDTO;
import com.CocOgreen.CenFra.MS.entity.Users;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role.roleId", target = "roleId")
    @Mapping(source = "role.roleName", target = "roleName")
    @Mapping(source = "location.locationId", target = "locationId")
    @Mapping(source = "location.locationName", target = "locationName")
    UsersDTO toDTO(Users user);

    @InheritInverseConfiguration
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    Users toEntity(UsersDTO dto);
}
