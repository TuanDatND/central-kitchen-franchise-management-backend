package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.ProductionOrderDTO;
import com.CocOgreen.CenFra.MS.entity.ProductionOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductionOrderMapper extends GenericMapper<ProductionOrder, ProductionOrderDTO> {
   @Override
   @Mapping(source = "Location.locationId", target = "locationId")
   @Mapping(source = "Location.locationName", target = "locationName")
   @Mapping(source = "Recipe.recipeId", target = "recipeId")
   @Mapping(source = "Recipe.recipeName", target = "recipeName")
   @Mapping(source = "ProductionStatus.statusName", target = "statusName")
   @Mapping(source = "ProductionStatus.statusId", target = "statusId")
   @Mapping(source = "createdBy.id", target = "createdById")
   @Mapping(source = "createdBy.fullName", target = "createdByName")
   @Mapping(source = "modifiedBy.id", target = "modifiedById")
   @Mapping(source = "modifiedBy.fullName", target = "modifiedByName")
   ProductionOrderDTO toDto(ProductionOrder productionOrder);

   @Override
   @Mapping(source = "locationId", target = "location.id")
   @Mapping(source = "recipeId", target = "recipe.id")
   @Mapping(source = "statusId", target = "status.id")
   @Mapping(target = "createdBy", ignore = true)
   @Mapping(target = "modifiedBy", ignore = true)
   @Mapping(target = "createdAt", ignore = true)
   @Mapping(target = "modifiedAt", ignore = true)
   ProductionOrder toEntity(ProductionOrderDTO productionOrderDTO);
}
