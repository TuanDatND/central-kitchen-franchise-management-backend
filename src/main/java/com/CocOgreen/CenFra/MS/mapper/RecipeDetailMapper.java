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
    @Mapping(source = "item.itemId", target = "inputItemId")
    @Mapping(source = "item.itemName", target = "inputItemName")
    RecipeDetailDTO toDto(RecipeDetail entity);

    @Override
    @Mapping(source = "inputItemId", target = "item.itemId")
    RecipeDetail toEntity(RecipeDetailDTO dto);


}
