package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.RecipeDTO;
import com.CocOgreen.CenFra.MS.entity.Recipe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
    Recipe toEntity(RecipeDTO dto);
    RecipeDTO toDTO(Recipe entity);
}
