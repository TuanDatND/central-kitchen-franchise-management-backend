package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.LocationTypeDTO;
import com.CocOgreen.CenFra.MS.entity.LocationType;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationTypeMapper {

    LocationTypeDTO toDTO(LocationType type);

    @InheritInverseConfiguration
    @Mapping(target = "locationTypeId", ignore = true)
    LocationType toEntity(LocationTypeDTO dto);
}
