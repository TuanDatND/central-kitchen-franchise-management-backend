package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.RecipeDetailDTO;
import com.CocOgreen.CenFra.MS.entity.RecipeDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockReservationMapper extends GenericMapper<RecipeDetail, RecipeDetailDTO> {

    @Override
    @Mapping(source = "Item.itemID", target = "itemId")
    @Mapping(source = "Item.itemName", target = "itemName")
    @Mapping(source = "Location.locationID", target = "locationId")
    RecipeDetailDTO toDto(RecipeDetail entity);

    @Override
    @Mapping(source = "itemId", target = "Item.itemID")
    @Mapping(source = "itemName", target = "Item.itemName")
    @Mapping(source = "locationId", target = "Location.locationID")
    RecipeDetail toEntity(RecipeDetailDTO dto);
}
