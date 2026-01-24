package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.ItemPriceHistoryDTO;
import com.CocOgreen.CenFra.MS.entity.ItemPriceHistory;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemPriceHistoryMapper {

    @Mapping(source = "item.itemId", target = "itemId")
    @Mapping(source = "item.itemName", target = "itemName")
    @Mapping(source = "item.sku", target = "sku")
    ItemPriceHistoryDTO toDTO(ItemPriceHistory price);

    @InheritInverseConfiguration
    @Mapping(target = "priceHistoryId", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    ItemPriceHistory toEntity(ItemPriceHistoryDTO dto);
}
