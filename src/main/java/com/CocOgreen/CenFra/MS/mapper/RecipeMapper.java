package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.RecipeDTO;
import com.CocOgreen.CenFra.MS.entity.Recipe;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {RecipeDetailMapper.class})
public interface RecipeMapper extends GenericMapper<Recipe,RecipeDTO> {

    @Override
    @Mapping(source = "outputItem.itemId",target = "outputItemId")
    @Mapping(source = "outputItem.itemName", target = "outputItemName")
    @Mapping(source = "recipeDetail", target = "details")
    RecipeDTO toDto(Recipe entity);

    @Override
    @Mapping(source = "outputItemId", target = "outputItem.itemId")
    Recipe toEntity(RecipeDTO dto);

    @AfterMapping
    default void linkDetails(@MappingTarget Recipe recipe) {
        if (recipe.getRecipeDetail() != null) {
            recipe.getRecipeDetail().forEach(detail -> detail.setRecipe(recipe));
        }
    }
}
