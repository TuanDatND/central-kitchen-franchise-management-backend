package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.ItemTypeDTO;
import com.CocOgreen.CenFra.MS.entity.ItemType;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemTypeMapper {

    ItemTypeDTO toDTO(ItemType type);

    @InheritInverseConfiguration
    @Mapping(target = "itemTypeId", ignore = true)
    ItemType toEntity(ItemTypeDTO dto);
}
