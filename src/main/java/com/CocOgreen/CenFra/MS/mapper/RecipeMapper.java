package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.RecipeDTO;
import com.CocOgreen.CenFra.MS.entity.Recipe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RecipeDetailMapper.class})
public interface RecipeMapper extends GenericMapper<Recipe,RecipeDTO> {

    @Override
    @Mapping(source = "outputItem.itemID",target = "outputItemId")
    @Mapping(source = "outputItem.itemName", target = "outputItemName")
    @Mapping(source = "recipeDetail", target = "details")
    RecipeDTO toDto(Recipe entity);

    @Override
    @Mapping(source = "outputItemId", target = "outputItem.itemID")
    Recipe toEntity(RecipeDTO dto);
}
