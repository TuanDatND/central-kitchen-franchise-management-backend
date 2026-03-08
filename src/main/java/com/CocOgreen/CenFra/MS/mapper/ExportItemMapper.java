package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.ExportItemDto;
import com.CocOgreen.CenFra.MS.entity.ExportItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExportItemMapper {
    @Mapping(target = "productId", source = "productBatch.product.productId")
    @Mapping(target = "productName", source = "productBatch.product.productName")
    @Mapping(target = "unit", source = "productBatch.product.unit.unitName")
    @Mapping(target = "batchCode", source = "productBatch.batchCode")
    @Mapping(target = "expiryDate", source = "productBatch.expiryDate")
    ExportItemDto toDto(ExportItem entity);

}
