package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.RecipeDetailDTO;
import com.CocOgreen.CenFra.MS.entity.RecipeDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockReservationMapper {
    RecipeDetailDTO toDTO(RecipeDetail entity);
    RecipeDetail toEntity(RecipeDetailDTO dto);
}
