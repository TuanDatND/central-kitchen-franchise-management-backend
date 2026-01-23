package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.ProductionOrderDTO;
import com.CocOgreen.CenFra.MS.entity.ProductionOrder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductionOrderMapper {
    ProductionOrder toEntity(ProductionOrderDTO dto);
    ProductionOrderDTO toDTO(ProductionOrder entity);
}
