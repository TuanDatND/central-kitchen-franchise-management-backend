package com.CocOgreen.CenFra.MS.mapper;

import com.CocOgreen.CenFra.MS.dto.response.ManuOrderResponse;
import com.CocOgreen.CenFra.MS.entity.ManufacturingOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ManufacturingOrderMapper {

    // Entity -> Response
    @Mapping(source = "createdBy.userId", target = "createdById")
    @Mapping(source = "createdBy.fullName", target = "createdByName")
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    ManuOrderResponse toResponse(ManufacturingOrder order);

}