package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.RecipeDetailDTO;
import com.CocOgreen.CenFra.MS.entity.Recipe;
import com.CocOgreen.CenFra.MS.entity.RecipeDetail;
import jakarta.persistence.Timeout;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecipeDetailMapper extends GenericMapper<RecipeDetail, RecipeDetailDTO>  {
    @Override
    @Mapping(source = "Item.itemId", target = "inputItemId")
    @Mapping(source = "Item.itemName", target = "inputItemName")
    RecipeDetailDTO toDto(RecipeDetail entity);

    @Override
    @Mapping(source = "outputItemId", target = "outputItem.id")
    RecipeDetail toEntity(RecipeDetailDTO dto);

    @AfterMapping
    default void linkDetails(@MappingTarget Recipe recipe) {
        if (recipe.getDetails() != null) {
            recipe.getDetails().forEach(detail -> detail.setRecipe(recipe));
        }
    }
}
