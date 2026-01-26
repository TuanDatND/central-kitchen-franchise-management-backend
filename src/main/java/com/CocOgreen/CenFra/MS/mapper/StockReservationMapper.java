package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.RecipeDetailDTO;
import com.CocOgreen.CenFra.MS.dto.StockReservationDTO;
import com.CocOgreen.CenFra.MS.entity.RecipeDetail;
import com.CocOgreen.CenFra.MS.entity.StockReservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockReservationMapper extends GenericMapper<StockReservation, StockReservationDTO> {

    @Override
    @Mapping(source = "item.itemId", target = "itemId")
    @Mapping(source = "item.itemName", target = "itemName")
    @Mapping(source = "location.locationId", target = "locationId")
    StockReservationDTO toDto(StockReservation entity);

    @Mapping(source = "itemId", target = "item.itemId")
    @Mapping(source = "itemName", target = "item.itemName")
    @Mapping(source = "locationId", target = "location.locationId")
    StockReservation toEntity(StockReservationDTO dto);
}
