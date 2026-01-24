package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.LocationDTO;
import com.CocOgreen.CenFra.MS.entity.Location;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    @Mapping(source = "locationType.locationTypeId", target = "locationTypeId")
    @Mapping(source = "locationType.code", target = "locationTypeCode")
    LocationDTO toDTO(Location location);

    @InheritInverseConfiguration
    @Mapping(target = "locationId", ignore = true)
    @Mapping(target = "locationType", ignore = true)
    Location toEntity(LocationDTO dto);
}
