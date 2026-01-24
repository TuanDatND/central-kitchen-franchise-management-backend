package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.UnitDTO;
import com.CocOgreen.CenFra.MS.entity.Unit;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    UnitDTO toDTO(Unit unit);

    @InheritInverseConfiguration
    @Mapping(target = "unitId", ignore = true)
    Unit toEntity(UnitDTO dto);
}

