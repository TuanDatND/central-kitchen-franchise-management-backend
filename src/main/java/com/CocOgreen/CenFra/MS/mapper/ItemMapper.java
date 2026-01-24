package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.ItemDTO;
import com.CocOgreen.CenFra.MS.entity.Item;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "itemType.itemTypeId", target = "itemTypeId")
    @Mapping(source = "itemType.code", target = "itemTypeCode")
    @Mapping(source = "unit.unitId", target = "unitId")
    @Mapping(source = "unit.unitName", target = "unitName")
    ItemDTO toDTO(Item item);

    @InheritInverseConfiguration
    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "itemType", ignore = true)
    @Mapping(target = "unit", ignore = true)
    Item toEntity(ItemDTO dto);
}
