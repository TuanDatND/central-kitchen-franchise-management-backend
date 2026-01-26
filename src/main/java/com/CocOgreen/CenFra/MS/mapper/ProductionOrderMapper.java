package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.ProductionOrderDTO;
import com.CocOgreen.CenFra.MS.entity.ProductionOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductionOrderMapper extends GenericMapper<ProductionOrder, ProductionOrderDTO> {
    @Override
    @Mapping(source = "location.locationId", target = "locationId")
    @Mapping(source = "location.locationName", target = "locationName")
    @Mapping(source = "recipe.outputItem.itemName", target = "productName")
    @Mapping(source = "status.code", target = "statusName")
    @Mapping(source = "status.productionStatusID", target = "statusId")
//   @Mapping(source = "createdBy.id", target = "createdById")
//   @Mapping(source = "createdBy.fullName", target = "createdByName")
//   @Mapping(source = "modifiedBy.id", target = "modifiedById")
//   @Mapping(source = "modifiedBy.fullName", target = "modifiedByName")
    ProductionOrderDTO toDto(ProductionOrder productionOrder);

    @Override
    @Mapping(source = "locationId", target = "location.locationId")
    @Mapping(source = "recipeId", target = "recipe.recipeId")
    @Mapping(source = "statusId", target = "status.productionStatusID")
    @Mapping(source = "statusName", target = "status.code")
//   @Mapping(target = "createdBy", ignore = true)
//   @Mapping(target = "modifiedBy", ignore = true)
//   @Mapping(target = "createdAt", ignore = true)
//   @Mapping(target = "modifiedAt", ignore = true)
    ProductionOrder toEntity(ProductionOrderDTO productionOrderDTO);
}
