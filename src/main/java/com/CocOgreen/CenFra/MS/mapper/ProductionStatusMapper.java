package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.ProductionStatusDTO;
import com.CocOgreen.CenFra.MS.entity.ProductionStatus;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductionStatusMapper {
    ProductionStatusDTO toDTO(ProductionStatus entity);
    ProductionStatus toEntity(ProductionStatusDTO dto);
}
