package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.InventoryDTO;
import com.CocOgreen.CenFra.MS.entity.Inventory;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "location.locationId", target = "locationId")
    @Mapping(source = "location.locationName", target = "locationName")
    @Mapping(source = "item.itemId", target = "itemId")
    @Mapping(source = "item.itemName", target = "itemName")
    @Mapping(source = "item.sku", target = "sku")
    InventoryDTO toDTO(Inventory inventory);

    @InheritInverseConfiguration
    @Mapping(target = "inventoryId", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    Inventory toEntity(InventoryDTO dto);
}
